package com.navisa.be.chat.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.chat.dto.message.ChatMessageResponse;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.support.WebSocketIntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.socket.WebSocketHttpHeaders;

@Sql(scripts = "/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class WebSocketConnectionTest extends WebSocketIntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저가 /user/chat/subscribe를 구독하면 Redis Pub/Sub을 통해 메시지를 수신한다")
    void verifyAutoSubscriptionOnConnect() throws Exception {
        // given
        User user = userTestFixture.createUser("ws@example.com", UserType.FILLED_FOREIGNER);
        String accessToken = jwtProvider.createAccessToken(user.getEmail());

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + accessToken);

        String url = String.format("ws://localhost:%d/ws", port);
        StompSession session = stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {})
                .get(20, TimeUnit.SECONDS);

        // 구독
        TestStompSessionHandler handler = new TestStompSessionHandler();
        session.subscribe("/user/chat/subscribe", handler);

        // 구독 등록이 되기까지의 암묵적 대기 처리
        TimeUnit.SECONDS.sleep(1);

        ChatMessageResponse message = new ChatMessageResponse(
                123L,
                1L,
                UUID.randomUUID(),
                user.getId(),
                "Test Message Content",
                MessageType.TEXT,
                ZonedDateTime.now(),
                LocalDateTime.now()
        );

        // when 1
        messagingTemplate.convertAndSendToUser(user.getId().toString(), "/chat/subscribe", message);

        // then 1
        ChatMessageResponse receivedDirect = (ChatMessageResponse) handler.completableFuture.get(10, TimeUnit.SECONDS);
        assertThat(receivedDirect.messageId()).isEqualTo(123L);
        assertThat(receivedDirect.content()).isEqualTo("Test Message Content");

        handler.reset(); // 수신용 Future 초기화

        String redisChannel = "user:ch:" + user.getId();
        String jsonMessage = objectMapper.writeValueAsString(message);

        // when 2
        redisTemplate.convertAndSend(redisChannel, jsonMessage);

        ChatMessageResponse receivedRedis = (ChatMessageResponse) handler.completableFuture.get(10, TimeUnit.SECONDS);

        // then 2
        assertThat(receivedRedis.messageId()).isEqualTo(123L);
        assertThat(receivedRedis.content()).isEqualTo("Test Message Content");

        session.disconnect();
    }

    @Test
    @DisplayName("토큰 없이 연결을 시도하면 실패한다")
    void verifyConnectionFailsWithoutToken() {
        String url = String.format("ws://localhost:%d/ws", port);
        StompHeaders connectHeaders = new StompHeaders(); // Token removed

        assertThatThrownBy(() -> stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS)).isInstanceOf(ExecutionException.class);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 연결을 시도하면 실패한다")
    void verifyConnectionFailsWithInvalidToken() {
        String url = String.format("ws://localhost:%d/ws", port);
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer invalid-token");

        assertThatThrownBy(() -> stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS)).isInstanceOf(ExecutionException.class);
    }
}
