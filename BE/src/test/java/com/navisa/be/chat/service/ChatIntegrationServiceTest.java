package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.message.ChatMessageResponse;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.repository.ChatMessageRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.ChatRoomTestFixture;
import com.navisa.be.support.ForeignerProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ChatIntegrationServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatIntegrationService chatIntegrationService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @MockitoSpyBean
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("메시지를 저장하고 Redis에 발행한다 (ChatRoom null 입력 -> 내부 조회) - AfterCommit")
    void saveAndPublishChatMessage_Success() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("a@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile,
                ChatRoomStatus.DEFAULT);

        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "Hello World",
                MessageType.TEXT);

        // when
        // Controller에서는 chatRoom을 null로 넘기고 내부에서 조회하도록 함
        chatIntegrationService.saveAndPublishChatMessage(foreignerUser.getId(), request, null);

        // then
        // 1. DB 저장 검증
        List<ChatMessage> messages = chatMessageRepository.findAll();
        // 기존에 createChatRoom 등에서 메시지가 생성되지 않았다면 1개여야 함.
        // Fixture가 메시지를 생성하지 않는다고 가정.
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("Hello World");
        assertThat(messages.get(0).getSenderId()).isEqualTo(foreignerProfile.getId());

        // 2. Redis 발행 검증 (Sender, Receiver 각각 1회씩)
        // afterCommit 훅이 실행되었어야 함
        verify(redisTemplate, times(1)).convertAndSend(eq("user:ch:" + foreignerUser.getId()),
                any(ChatMessageResponse.class));
        verify(redisTemplate, times(1)).convertAndSend(eq("user:ch:" + agentUser.getId()),
                any(ChatMessageResponse.class));
    }

    @Test
    @DisplayName("읽음 처리 메시지를 저장하고 Redis에 발행한다")
    void saveAndPublishReadEventMessage_Success() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("a@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile,
                ChatRoomStatus.DEFAULT);

        // 메시지 2개 생성 (읽지 않은 상태)
        ChatMessage msg1 = chatRoomTestFixture.createChatMessage(chatRoom, agentProfile.getId(), "Msg1", false);
        ChatMessage msg2 = chatRoomTestFixture.createChatMessage(chatRoom, agentProfile.getId(), "Msg2", false);

        // 읽음 처리 요청 (msg2까지 읽었다고 가정)
        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                String.valueOf(msg2.getId()), // content field used for lastReadMessageId
                MessageType.READ);

        // when
        chatIntegrationService.saveAndPublishReadEventMessage(foreignerUser.getId(), request);

        // then
        // 1. DB 읽음 상태 검증
        List<ChatMessage> messages = chatMessageRepository.findAll();
        assertThat(messages).allMatch(ChatMessage::getIsReadByOther);

        // 2. Redis 발행 검증
        verify(redisTemplate, times(1)).convertAndSend(eq("user:ch:" + foreignerUser.getId()),
                any(ChatMessageResponse.class));
        verify(redisTemplate, times(1)).convertAndSend(eq("user:ch:" + agentUser.getId()),
                any(ChatMessageResponse.class));
    }
}
