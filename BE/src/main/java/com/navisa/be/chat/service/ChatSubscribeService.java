package com.navisa.be.chat.service;

import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.infra.redis.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatSubscribeService {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;

    // 로컬 서버 인스턴스의 웹소켓 세션 관리용
    private final Map<UUID, Set<String>> localSessions = new ConcurrentHashMap<>();

    // 유저별 고유 락 객체 보관용
    private final Map<UUID, Object> userLocks = new ConcurrentHashMap<>();

    private Object getUserLock(UUID userId) {
        return userLocks.computeIfAbsent(userId, k -> new Object());
    }

    public void addChatSubscription(UUID userId, String sessionId) {
        if (userId == null || sessionId == null)
            return;

        synchronized (getUserLock(userId)) {
            Set<String> sessions = localSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet());
            boolean isFirstSession = sessions.isEmpty();
            sessions.add(sessionId);

            if (isFirstSession) {
                subscribeUserAllRooms(userId);
            }
        }
    }

    public void removeChatSubscription(UUID userId, String sessionId) {
        if (userId == null || sessionId == null)
            return;

        synchronized (getUserLock(userId)) {
            Set<String> sessions = localSessions.get(userId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    unsubscribeUserAllRooms(userId);
                    localSessions.remove(userId);
                    userLocks.remove(userId); // Lock 자원 정리
                }
            }
        }
    }

    /**
     * 서버가 해당 유저를 위한 메시지를 수신하기 위한 준비 로직
     */
    private void subscribeUserAllRooms(UUID userId) {
        if (userId == null) {
            throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
        }

        // Redis Pub/Sub 리스너에 유저 전용 채널 등록, ListenerContainer에 리스너와 토픽을 동적으로 등록
        // 이제부터 Redis에서 "user:ch:{uuid}"로 Publish되는 모든 메시지는 RedisSubscriber가 받음
        ChannelTopic userTopic = new ChannelTopic("user:ch:" + userId);
        redisMessageListenerContainer.addMessageListener(redisSubscriber, userTopic);
    }

    /**
     * 유저의 구독 정보를 정리하는 비즈니스 로직
     */
    private void unsubscribeUserAllRooms(UUID userId) {
        if (userId == null) {
            throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
        }

        ChannelTopic topic = new ChannelTopic("user:ch:" + userId.toString());
        redisMessageListenerContainer.removeMessageListener(redisSubscriber, topic);
    }
}
