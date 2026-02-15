package com.navisa.be.chat.service;

import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.infra.redis.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatSubscribeService {
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisSubscriber redisSubscriber;
    private final StringRedisTemplate redisTemplate;

    private static final String SESSION_COUNT_KEY_PREFIX = "user:ws:sessions:";

    /**
     * 서버가 해당 유저를 위한 메시지를 수신하기 위한 준비 로직
     */
    @Transactional(readOnly = true)
    public void subscribeUserAllRooms(UUID userId) {
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
    public void unsubscribeUserAllRooms(UUID userId) {
        if (userId == null) {
            throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
        }

        ChannelTopic topic = new ChannelTopic("user:ch:" + userId.toString());
        redisMessageListenerContainer.removeMessageListener(redisSubscriber, topic);
    }

    public void increaseSessionCount(UUID userId) {
        if (userId == null)
            return;

        redisTemplate.opsForValue().increment(SESSION_COUNT_KEY_PREFIX + userId);
    }

    public void decreaseSessionCount(UUID userId) {
        if (userId == null)
            return;

        String key = SESSION_COUNT_KEY_PREFIX + userId;
        Long count = redisTemplate.opsForValue().decrement(key);

        if (count != null && count <= 0) {
            unsubscribeUserAllRooms(userId);
            redisTemplate.delete(key);
        }
    }
}
