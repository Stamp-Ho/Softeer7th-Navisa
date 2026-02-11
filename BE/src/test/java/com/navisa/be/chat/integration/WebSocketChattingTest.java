package com.navisa.be.chat.integration;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.message.ChatMessageResponse;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.socket.WebSocketHttpHeaders;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@Sql(scripts = "/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
public class WebSocketChattingTest extends WebSocketIntegrationTestSupport {

    private final String URL = "ws://localhost:%d/ws";

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Test
    @DisplayName("행정사가 외국인에게 메시지를 전송하면 외국인 구독 경로로 메시지가 수신된다")
    void testMessageDeliveryFromAgentToForeigner() throws Exception {
        // given 유저 및 채팅방 준비
        User agentUser = userTestFixture.createUser("agent@example.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("박행정", "서울시", agentUser.getId());
        User foreignerUser = userTestFixture.createUser("foreigner@example.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        //  두 사용자 모두 연결
        String agentToken = jwtProvider.createAccessToken(agentUser.getEmail());
        String foreignerToken = jwtProvider.createAccessToken(foreignerUser.getEmail());
        StompSession agentSession = connectSession(agentToken);
        StompSession foreignerSession = connectSession(foreignerToken);

        // 수신 확인을 위한 객체
        AtomicReference<ChatMessageResponse> receivedMessage = new AtomicReference<>();

        // 외국인이 자신의 개인 채널을 구독
        CompletableFuture<ChatMessageResponse> foreignerFuture = new CompletableFuture<>();
        foreignerSession.subscribe("/user/chat/subscribe", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                receivedMessage.set((ChatMessageResponse) payload); // 메시지 도착 시 데이터 채움
            }
        });

        Thread.sleep(1000);

        // when 행정사가 외국인에게 메시지 발송
        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "수임 제안합니다.",
                MessageType.PROPOSAL,
                ZonedDateTime.now()
        );
        agentSession.send("/pub/chat/message", request);

        // then 외국인이 메시지를 받았는지 확인
        await()
                .atMost(20, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS) // 0.1초마다 조건 확인
                .untilAsserted(() -> {
                    ChatMessageResponse response = receivedMessage.get();

                    assertThat(response).isNotNull();
                    assertThat(response.type()).isEqualTo(MessageType.PROPOSAL);
                    assertThat(response.clientMessageId()).isEqualTo(request.clientMessageId());
                    assertThat(response.receiverId()).isEqualTo(foreignerUser.getId());
                });

        agentSession.disconnect();
        foreignerSession.disconnect();
    }

    // 중복 코드를 줄이기 위한 연결 헬퍼 메서드
    private StompSession connectSession(String token) throws Exception {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);
        return stompClient
                .connectAsync(String.format(URL, port), new WebSocketHttpHeaders(), connectHeaders, new StompSessionHandlerAdapter() {
                })
                .get(20, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("행정사가 여러 기기(세션)로 접속했을 때, 한 곳에서 보낸 메시지가 모든 세션에 에코된다")
    void testMessageEchoToMultipleSessions() throws Exception {
        // given: 행정사 유저 및 채팅방 준비
        User agentUser = userTestFixture.createUser("agent_multi@example.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("박행정", "서울시", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner_multi@example.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(
                foreignerProfile,
                agentProfile,
                ChatRoomStatus.DEFAULT,
                ZonedDateTime.now()
        );

        String agentToken = jwtProvider.createAccessToken(agentUser.getEmail());

        // 행정사의 두 가지 세션 연결
        StompSession agentSession1 = connectSession(agentToken);
        StompSession agentSession2 = connectSession(agentToken);

        // 두 세션 모두 자신의 개인 채널을 구독
        CompletableFuture<ChatMessageResponse> echoFuture1 = new CompletableFuture<>();
        CompletableFuture<ChatMessageResponse> echoFuture2 = new CompletableFuture<>();

        agentSession1.subscribe("/user/chat/subscribe", new CustomFrameHandler(echoFuture1));
        agentSession2.subscribe("/user/chat/subscribe", new CustomFrameHandler(echoFuture2));

        // when: 행정사가 세션 1을 통해 외국인에게 메시지 발송
        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "멀티 세션 테스트 메시지입니다.",
                MessageType.TEXT,
                ZonedDateTime.now()
        );
        agentSession1.send("/pub/chat/message", request);

        // then: 메시지를 보낸 세션1과 대기 중이던 세션2 모두 메시지를 받아야 함
        ChatMessageResponse response1 = echoFuture1.get(10, TimeUnit.SECONDS);
        ChatMessageResponse response2 = echoFuture2.get(10, TimeUnit.SECONDS);

        // 검증: 두 응답 모두 동일한 메시지인지 확인
        assertThat(response1.content()).isEqualTo("멀티 세션 테스트 메시지입니다.");
        assertThat(response2.content()).isEqualTo("멀티 세션 테스트 메시지입니다.");
        assertThat(response1.receiverId()).isEqualTo(agentUser.getId()); // 본인이 수신자인지 확인
        assertThat(response2.receiverId()).isEqualTo(agentUser.getId());

        agentSession1.disconnect();
        agentSession2.disconnect();
    }

    /**
     * 테스트 코드 가독성을 위한 재사용 가능한 FrameHandler
     */
    @RequiredArgsConstructor
    private static class CustomFrameHandler implements StompFrameHandler {
        private final CompletableFuture<ChatMessageResponse> future;

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return ChatMessageResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            future.complete((ChatMessageResponse) payload);
        }
    }
}