package com.navisa.be.chat.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.navisa.be.support.WebSocketIntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;

class WebSocketAuthTest extends WebSocketIntegrationTestSupport {

    @Test
    @DisplayName("유효한 토큰으로 연결을 시도하면 성공한다")
    void connect_WithValidToken_Succeeds() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        User user = userTestFixture.createUser("valid@example.com", UserType.FILLED_FOREIGNER);
        String accessToken = jwtProvider.createAccessToken(user.getEmail(), user.getId(), user.getUserType());

        String url = String.format("ws://localhost:%d/ws", port);
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + accessToken);

        // when
        StompSession session = stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
                })
                .get(5, TimeUnit.SECONDS);

        // then
        assertThat(session.isConnected()).isTrue();
        session.disconnect();
    }

    @Test
    @DisplayName("토큰 없이 연결을 시도하면 실패한다")
    void connect_WithoutToken_Fails() {
        // given
        String url = String.format("ws://localhost:%d/ws", port);
        StompHeaders connectHeaders = new StompHeaders(); // Token missing

        // when & then
        assertThatThrownBy(() -> stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
                })
                .get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 연결을 시도하면 실패한다")
    void connect_WithInvalidToken_Fails() {
        // given
        String url = String.format("ws://localhost:%d/ws", port);
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("token", "invalid-token");

        // when & then
        assertThatThrownBy(() -> stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
                })
                .get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class);
    }

    @Test
    @DisplayName("유효하지 않은 유저 타입(UNFILLED_FOREIGNER)으로 연결을 시도하면 실패한다")
    void connect_WithInvalidUserType_Fails() {
        // given
        User user = userTestFixture.createUser("unfilled@example.com", UserType.UNFILLED_FOREIGNER);
        String accessToken = jwtProvider.createAccessToken(user.getEmail(), user.getId(), user.getUserType());

        String url = String.format("ws://localhost:%d/ws", port);
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("token", accessToken);

        // when & then
        assertThatThrownBy(() -> stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
                })
                .get(5, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class);
    }
}
