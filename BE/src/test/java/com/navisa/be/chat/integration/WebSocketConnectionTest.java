package com.navisa.be.chat.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.socket.WebSocketHttpHeaders;

@Sql(scripts = "/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class WebSocketConnectionTest extends WebSocketIntegrationTestSupport {

    @Test
    @DisplayName("유저가 /chat/sub을 구독하면 Redis Pub/Sub을 통해 메시지를 수신한다")
    void verifyAutoSubscriptionOnConnect() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        User user = userTestFixture.createUser("ws@example.com", UserType.FILLED_FOREIGNER);
        String accessToken = jwtProvider.createAccessToken(user.getEmail());

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("token", accessToken);

        // when
        String url = String.format("ws://localhost:%d/ws", port);
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        StompSession session = stompClient
                .connectAsync(url, httpHeaders, connectHeaders, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);

        // 구독
        TestStompSessionHandler handler = new TestStompSessionHandler();
        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination("/user/chat/subscribe");
        session.subscribe(subscribeHeaders, handler);

        // 구독 등록이 되기까지의 암묵적 대기 처리
        TimeUnit.SECONDS.sleep(1);

        // 검증 1: Direct Send to User
        String directMessage = "Direct Message Content";
        messagingTemplate.convertAndSendToUser(user.getId().toString(), "/chat/subscribe", directMessage);

        String receivedMessage = handler.completableFuture.get(5, TimeUnit.SECONDS);
        assertThat(receivedMessage).isEqualTo(directMessage);

        // 검증 2: Redis Pub/Sub -> WebSocket
        handler.reset(); // 다른 테스트에서도 핸들러 재사용을 위한 메세지 초기화

        String redisChannel = "user:ch:" + user.getId();
        String testMessage = "Test Message Content";
        redisTemplate.convertAndSend(redisChannel, testMessage);

        // then - Verify message receipt
        String receivedRedisMessage = handler.completableFuture.get(5, TimeUnit.SECONDS);
        assertThat(receivedRedisMessage).isEqualTo(testMessage);

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
        connectHeaders.add("token", "invalid-token");

        assertThatThrownBy(() -> stompClient
                .connectAsync(url, new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS)).isInstanceOf(ExecutionException.class);
    }

    @Test
    @DisplayName("Public Topic 구독 및 수신 테스트")
    void verifyPublicSubscription() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        User user = userTestFixture.createUser("public@example.com", UserType.FILLED_FOREIGNER);
        String accessToken = jwtProvider.createAccessToken(user.getEmail());
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("token", accessToken);

        // when
        String url = String.format("ws://localhost:%d/ws", port);
        WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders();
        StompSession session = stompClient
                .connectAsync(url, httpHeaders, connectHeaders, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);

        // 1. 구독
        TestStompSessionHandler handler = new TestStompSessionHandler();
        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination("/chat/test");
        session.subscribe(subscribeHeaders, handler);

        // 2. 전송 (Client -> Server)
        String message = "Public Message";
        session.send("/chat/test", message);

        // then
        String receivedMessage = handler.completableFuture.get(5, TimeUnit.SECONDS);
        assertThat(receivedMessage).isEqualTo(message);

        // 간단한 수신 검증: Server -> Client
        handler.reset(); // 다른 웹소켓 통합 테스트에서 해당 테스트 핸들러를 재사용하기 위한 초기화
        String serverMessage = "Server Public Message";
        messagingTemplate.convertAndSend("/chat/test", serverMessage);

        String receivedServerMessage = handler.completableFuture.get(5, TimeUnit.SECONDS);
        assertThat(receivedServerMessage).isEqualTo(serverMessage);

        session.disconnect();
    }
}
