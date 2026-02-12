package com.navisa.be.chat.integration;

import com.navisa.be.support.WebSocketIntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketSessionManagementTest extends WebSocketIntegrationTestSupport {

    @Test
    void sessionCountShouldIncrementAndDecrement() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        User user = userTestFixture.createUser("test@example.com", UserType.FILLED_FOREIGNER);
        String token = jwtProvider.createAccessToken(user.getEmail());
        UUID userId = user.getId();

        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + token);

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        // When - Connect Session 1
        StompSession session1 = stompClient.connectAsync("ws://localhost:" + port + "/ws",
                handshakeHeaders, headers, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);

        // Then - Session count should be 1
        String key = "user:ws:sessions:" + userId;

        // Wait for async processing
        org.awaitility.Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            String count1 = redisTemplate.opsForValue().get(key);
            assertThat(count1).isEqualTo("1");
        });

        // When - Connect Session 2
        StompSession session2 = stompClient.connectAsync("ws://localhost:" + port + "/ws",
                handshakeHeaders, headers, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);

        // Then - Session count should be 2
        org.awaitility.Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            String count2 = redisTemplate.opsForValue().get(key);
            assertThat(count2).isEqualTo("2");
        });

        // When - Disconnect Session 1
        session1.disconnect();

        // Then - Session count should be 1
        org.awaitility.Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            String count3 = redisTemplate.opsForValue().get(key);
            assertThat(count3).isEqualTo("1");
        });

        // When - Disconnect Session 2
        session2.disconnect();

        // Then - Session count should be 0 (key deleted)
        org.awaitility.Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Boolean hasKey = redisTemplate.hasKey(key);
            assertThat(hasKey).isFalse();
        });
    }
}
