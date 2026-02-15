package com.navisa.be.chat.service;

import com.navisa.be.global.infra.redis.RedisSubscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatSubscribeServiceTest {

    @InjectMocks
    private ChatSubscribeService chatSubscribeService;

    @Mock
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Mock
    private RedisSubscriber redisSubscriber;

    @Test
    @DisplayName("유저 ID로 Redis Listener에 등록한다")
    void subscribeUserAllRooms() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        chatSubscribeService.subscribeUserAllRooms(userId);

        // then
        verify(redisMessageListenerContainer).addMessageListener(
                eq(redisSubscriber),
                any(ChannelTopic.class));
    }

    @Test
    @DisplayName("채팅방이 없는 경우에도 Redis Listener에 등록한다 (빈 리스트라도 구독)")
    void subscribeUserAllRooms_Empty() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        chatSubscribeService.subscribeUserAllRooms(userId);

        // then
        // 채팅방 유무와 상관없이 topic을 구독하도록 변경되었으므로 addMessageListener가 호출되어야 함
        verify(redisMessageListenerContainer).addMessageListener(
                eq(redisSubscriber),
                any(ChannelTopic.class));
    }

    @Test
    @DisplayName("구독 해제 시 리스너를 제거한다")
    void unsubscribeUserAllRooms() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        chatSubscribeService.unsubscribeUserAllRooms(userId);

        // then
        verify(redisMessageListenerContainer).removeMessageListener(
                eq(redisSubscriber),
                any(ChannelTopic.class));
    }
}
