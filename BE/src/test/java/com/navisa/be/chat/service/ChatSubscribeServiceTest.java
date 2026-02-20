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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    @DisplayName("여러 기기에서 동시에 구독해도 Redis 리스너는 1번만 등록된다")
    void addChatSubscription_Concurrent_ShouldSubscribeOnce() throws InterruptedException {
        // given
        UUID userId = UUID.randomUUID();
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            String sessionId = "session-" + i;
            executorService.submit(() -> {
                try {
                    chatSubscribeService.addChatSubscription(userId, sessionId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        verify(redisMessageListenerContainer, times(1)).addMessageListener(
                eq(redisSubscriber),
                any(ChannelTopic.class));
        executorService.shutdown();
    }

    @Test
    @DisplayName("여러 기기의 세션이 연결/해제되어도 마지막 1개만 남으면 리스너가 유지되고 모두 끊어지면 해제된다")
    void addChatSubscriptionAndRemoveChatSubscription_MultiSession() {
        // given
        UUID userId = UUID.randomUUID();
        String session1 = "test-session-1";
        String session2 = "test-session-2";

        // when: 첫 번째 세션 연결
        chatSubscribeService.addChatSubscription(userId, session1);
        verify(redisMessageListenerContainer, times(1)).addMessageListener(any(), any(ChannelTopic.class));

        // when: 두 번째 세션 연결
        chatSubscribeService.addChatSubscription(userId, session2);
        // 이미 등록된 상태이므로 addMessageListener는 더 이상 호출되지 않아야 함 (times 1 유지)
        verify(redisMessageListenerContainer, times(1)).addMessageListener(any(), any(ChannelTopic.class));

        // when: 첫 번째 세션 해제
        chatSubscribeService.removeChatSubscription(userId, session1);
        // 세션1이 끊여져도 세션2가 남아있으므로 removeMessageListener는 호출되지 않아야 함 (times 0)
        verify(redisMessageListenerContainer, never()).removeMessageListener(any(), any(ChannelTopic.class));

        // when: 마지막 세션 해제
        chatSubscribeService.removeChatSubscription(userId, session2);
        // 이제 모든 세션이 끊어졌으므로 removeMessageListener가 1번 호출되어야 함
        verify(redisMessageListenerContainer, times(1)).removeMessageListener(any(), any(ChannelTopic.class));
    }

    @Test
    @DisplayName("유저 ID로 첫 채팅 구독 시 Redis Listener에 등록한다")
    void addChatSubscription_FirstSession_ShouldSubscribe() {
        // given
        UUID userId = UUID.randomUUID();
        String sessionId = "test-session-1";

        // when
        chatSubscribeService.addChatSubscription(userId, sessionId);

        // then
        verify(redisMessageListenerContainer).addMessageListener(
                eq(redisSubscriber),
                any(ChannelTopic.class));
    }

    @Test
    @DisplayName("구독 해제 시 남은 세션이 없으면 리스너를 제거한다")
    void removeChatSubscription_shouldRemoveLocalListener_WhenLocalSessionEmpty() {
        // given
        UUID userId = UUID.randomUUID();
        String sessionId = "test-session-1";
        chatSubscribeService.addChatSubscription(userId, sessionId);

        // when
        chatSubscribeService.removeChatSubscription(userId, sessionId);

        // then
        verify(redisMessageListenerContainer).removeMessageListener(
                eq(redisSubscriber),
                any(ChannelTopic.class));
    }

    @Test
    @DisplayName("null 파라미터 방어")
    void testNullParameters() {
        // given
        UUID userId = UUID.randomUUID();
        String sessionId = "test-session";

        // when & then
        chatSubscribeService.addChatSubscription(null, sessionId);
        chatSubscribeService.addChatSubscription(userId, null);
        chatSubscribeService.addChatSubscription(null, null);

        chatSubscribeService.removeChatSubscription(null, sessionId);
        chatSubscribeService.removeChatSubscription(userId, null);
        chatSubscribeService.removeChatSubscription(null, null);

        // Redis 로직이 호출되지 않았음을 확인
        verify(redisMessageListenerContainer, never()).addMessageListener(any(), any(ChannelTopic.class));
        verify(redisMessageListenerContainer, never()).removeMessageListener(any(), any(ChannelTopic.class));
    }
}
