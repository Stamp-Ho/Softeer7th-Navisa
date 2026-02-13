package com.navisa.be.support;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.navisa.be.auth.jwt.JwtProvider;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.navisa.be.chat.dto.message.ChatMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class WebSocketIntegrationTestSupport extends IntegrationTestSupport {

    @LocalServerPort
    protected int port;

    @Autowired
    protected JwtProvider jwtProvider;

    @Autowired
    protected UserTestFixture userTestFixture;

    @Autowired
    protected StringRedisTemplate redisTemplate;

    @Autowired
    protected SimpMessagingTemplate messagingTemplate;

    @Autowired
    protected SimpUserRegistry simpUserRegistry;

    protected WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() {
        // HTTP 예비 요청 없이 웹소켓으로 요청
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(standardWebSocketClient);

        //  JSON 직렬화/역직렬화를 위한 컨버터 설정
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // JSR-310 모듈 등록
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 형식 사용

        converter.setObjectMapper(mapper);
        stompClient.setMessageConverter(converter);
    }

    public static class TestStompSessionHandler extends StompSessionHandlerAdapter {
        public CompletableFuture<ChatMessageResponse> completableFuture = new CompletableFuture<>();

        public void reset() {
            completableFuture = new CompletableFuture<>();
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ChatMessageResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            completableFuture.complete((ChatMessageResponse) payload);
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                Throwable exception) {
            exception.printStackTrace();
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            exception.printStackTrace();
        }
    }
}
