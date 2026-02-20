package com.navisa.be.chat.integration;

import com.navisa.be.support.WebSocketIntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class WebSocketSessionManagementTest extends WebSocketIntegrationTestSupport {

    private StompSession session1;
    private StompSession session2;

    @AfterEach
    void tearDown() {
        if (session1 != null && session1.isConnected()) {
            session1.disconnect();
        }
        if (session2 != null && session2.isConnected()) {
            session2.disconnect();
        }
    }

    @Test
    void sessionCountShouldIncrementAndDecrement() throws ExecutionException, InterruptedException, TimeoutException {
        // Given
        User user = userTestFixture.createUser("test@example.com", UserType.FILLED_FOREIGNER);
        String token = jwtProvider.createAccessToken(user.getEmail());
        String userIdStr = user.getId().toString();

        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", "Bearer " + token);

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();

        // When
        session1 = stompClient.connectAsync("ws://localhost:" + port + "/ws",
                handshakeHeaders, headers, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var simpUser = simpUserRegistry.getUser(userIdStr);
            assertThat(simpUser).isNotNull();
            assertThat(simpUser.getSessions()).hasSize(1);
        });

        // When
        session2 = stompClient.connectAsync("ws://localhost:" + port + "/ws",
                handshakeHeaders, headers, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var simpUser = simpUserRegistry.getUser(userIdStr);
            assertThat(simpUser).isNotNull();
            assertThat(simpUser.getSessions()).hasSize(2);
        });

        // When
        session1.disconnect();

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var simpUser = simpUserRegistry.getUser(userIdStr);
            assertThat(simpUser).isNotNull();
            assertThat(simpUser.getSessions()).hasSize(1);
        });

        // When
        session2.disconnect();

        // Then
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var simpUser = simpUserRegistry.getUser(userIdStr);
            assertThat(simpUser).isNull();
        });
    }
}
