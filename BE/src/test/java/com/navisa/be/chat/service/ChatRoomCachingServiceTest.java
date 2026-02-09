package com.navisa.be.chat.service;

import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatRoomCachingServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomCachingService chatRoomCachingService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("채팅방 ID 목록을 Redis에 저장하고 조회할 수 있다")
    void addAndGetChatRooms() {
        // given
        UUID userId = UUID.randomUUID();
        List<Long> roomIds = List.of(100L, 200L, 300L);

        // when
        chatRoomCachingService.addChatRoomsToUser(userId, roomIds);

        // then
        Set<Long> result = chatRoomCachingService.getUserChatRoomIds(userId);
        assertThat(result).containsExactlyInAnyOrderElementsOf(roomIds);
    }

    @Test
    @DisplayName("유저의 Redis 구독 정보를 삭제하면 더 이상 조회되지 않는다")
    void removeAllChatRooms() {
        // given
        UUID userId = UUID.randomUUID();
        List<Long> roomIds = List.of(1L, 2L);
        chatRoomCachingService.addChatRoomsToUser(userId, roomIds);

        // when
        chatRoomCachingService.removeAllChatRoomsFromUser(userId);

        // then
        Set<Long> result = chatRoomCachingService.getUserChatRoomIds(userId);
        assertThat(result).isEmpty();
    }
}
