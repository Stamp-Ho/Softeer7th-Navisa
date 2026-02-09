package com.navisa.be.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomCachingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String USER_CHANNELS_PREFIX = "user:channels:";

    /**
     * 유저가 참여 중인 채팅방 ID 목록(List)을 한 번에 추가
     */
    public void addChatRoomsToUser(UUID userId, List<Long> chatRoomIds) {
        if (chatRoomIds == null || chatRoomIds.isEmpty()) return;

        String key = USER_CHANNELS_PREFIX + userId;
        // Long 리스트를 String 배열로 변환하여 SADD 한 번에 실행
        String[] roomIdArray = chatRoomIds.stream()
                .map(Object::toString)
                .toArray(String[]::new);

        redisTemplate.opsForSet().add(key, (Object[]) roomIdArray);
    }

    /**
     * 유저가 구독해야 할 모든 채팅방(채널) ID 목록을 조회
     */
    public Set<Long> getUserChatRoomIds(UUID userId) {
        String key = USER_CHANNELS_PREFIX + userId;
        Set<Object> members = redisTemplate.opsForSet().members(key);

        if (members == null) return Set.of();

        return members.stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toSet());
    }

    /**
     * 웹소켓 연결 해제 시 해당 유저의 모든 채널 정보를 한 번에 삭제
     */
    public void removeAllChatRoomsFromUser(UUID userId) {
        if (userId == null) return;

        String key = USER_CHANNELS_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
