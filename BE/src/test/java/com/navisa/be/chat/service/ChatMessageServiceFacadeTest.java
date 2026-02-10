package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.response.ChatMessageCountResponse;
import com.navisa.be.chat.dto.response.ChatMessageSimpleResponse;
import com.navisa.be.chat.exception.ChatMessageException;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ChatMessageRepository;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.common.model.enums.ResponseStatus;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ChatMessageServiceFacadeTest extends IntegrationTestSupport {

    @Autowired
    private ChatMessageServiceFacade chatMessageServiceFacade;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("Foreigner 이메일로 안 읽은 메시지 수를 조회한다")
    void findNonReadCountByUserEmail_Foreigner() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("a@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile,
                ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        // Agent가 보낸 메시지 2개 (안 읽음)
        chatRoomTestFixture.createChatMessage(chatRoom, agentProfile.getId(), "Msg1", false);
        chatRoomTestFixture.createChatMessage(chatRoom, agentProfile.getId(), "Msg2", false);

        // Foreigner가 보낸 메시지 1개
        chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "My Msg", false);

        // when
        ChatMessageCountResponse response = chatMessageServiceFacade
                .findNonReadCountByUserEmail(foreignerUser.getEmail());

        // then
        assertThat(response.count()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Agent 이메일로 안 읽은 메시지 수를 조회한다")
    void findNonReadCountByUserEmail_Agent() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("a@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile,
                ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        // Foreigner가 보낸 메시지 3개 (안 읽음)
        chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "Msg1", false);
        chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "Msg2", false);
        chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "Msg3", false);

        // when
        ChatMessageCountResponse response = chatMessageServiceFacade
                .findNonReadCountByUserEmail(agentUser.getEmail());

        // then
        assertThat(response.count()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Agent 이메일로 매칭된(메시지가 있는) 방의 수를 조회한다")
    void findMatchedNonReadCountByUserEmail() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("a@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr",
                agentUser.getId());

        // 매칭된 방 (메시지 있음)
        ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile,
                ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());
        Proposal proposal1 = new Proposal(chatRoom1, agentProfile.getId());
        ReflectionTestUtils.setField(proposal1, "status", ProposalStatus.MATCHED);
        proposalRepository.save(proposal1);

        chatRoomTestFixture.createChatMessage(chatRoom1, foreignerProfile.getId(), "Hello", false);

        // 매칭 안된 방 (메시지 없음)
        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        // when
        ChatMessageCountResponse response = chatMessageServiceFacade
                .findMatchedNonReadCountByUserEmail(agentUser.getEmail());

        // then
        assertThat(response.count()).isEqualTo(1L);
    }

    @Test
    @DisplayName("채팅방 메시지를 No-Offset 방식으로 조회하고 SliceResponse로 반환한다")
    void findChatMessagesByChatRoomIdAndNoOffset() {
        // given
        // 유저 및 프로필 생성
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile,
                ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // 시간 기준 설정
        LocalDateTime now = LocalDateTime.now();

        // 30개의 메시지 생성 (과거 -> 최신 순으로 시간 주입)
        for (int i = 1; i <= 30; i++) {
            ChatMessage message = chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "Message " + i, false);
            ReflectionTestUtils.setField(message, "createdAt", now.plusMinutes(i));
            chatMessageRepository.save(message);
        }

        // when - 첫 페이지 조회 (10개 요청)
        // foreignerUser.getEmail()로 조회하므로 자신이 보낸 메시지 여부 판단 기준이 됨
        SliceRequest<Long> sliceRequest = new SliceRequest<>(null, 10);
        SliceResponse<ChatMessageSimpleResponse, Long> response = chatMessageServiceFacade
                .findChatMessagesByChatRoomIdAndNoOffset(foreignerUser.getEmail(), chatRoom.getId(), sliceRequest);

        // then - SliceResponse 및 데이터 검증
        assertThat(response.content()).hasSize(10); // 요청한 10개 반환 확인
        assertThat(response.existsNext()).isTrue(); // 30개 중 10개만 가져왔으므로 다음 페이지 존재

        // 최신순 정렬 확인 (가장 나중에 생성된 Message 30이 첫 번째)
        assertThat(response.content().get(0).content()).isEqualTo("Message 30");
        assertThat(response.content().get(9).content()).isEqualTo("Message 21");

        // DTO 매핑 및 비즈니스 로직 확인
        ChatMessageSimpleResponse firstMsg = response.content().get(0);
        // Foreigner가 조회 중이고, 메시지 작성자도 Foreigner이므로 true여야 함
        assertThat(firstMsg.isSentByMe()).isTrue();
    }

    @Test
    @DisplayName("Foreigner가 자신이 속하지 않은 채팅방의 메시지를 조회하면 예외가 발생한다")
    void findChatMessagesByChatRoomIdAndNoOffset_Foreigner_AccessDenied() {
        // given
        User foreignerUserA = userTestFixture.createUser("userA@test.com", UserType.FILLED_FOREIGNER);
        User foreignerUserB = userTestFixture.createUser("userB@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfileA = foreignerProfileTestFixture.createForeignerProfile(foreignerUserA);
        ForeignerProfile foreignerProfileB = foreignerProfileTestFixture.createForeignerProfile(foreignerUserB);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr",
                agentUser.getId());

        // foreignerUserB가 속한 채팅방 생성
        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfileB, agentProfile,
                ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        SliceRequest<Long> sliceRequest = new SliceRequest<>(null, 10);

        // when & then
        // foreignerUserA가 foreignerUserB의 채팅방을 조회하려고 시도
        assertThatThrownBy(() -> chatMessageServiceFacade
                .findChatMessagesByChatRoomIdAndNoOffset(foreignerUserA.getEmail(), chatRoom.getId(),
                        sliceRequest))
                .isInstanceOf(ChatMessageException.class)
                .hasMessage(ResponseStatus.NOT_ALLOWED_TO_GET_CHAT_MESSAGE.getMessage());
    }

    @Test
    @DisplayName("Agent가 자신이 속하지 않은 채팅방의 메시지를 조회하면 예외가 발생한다")
    void findChatMessagesByChatRoomIdAndNoOffset_Agent_AccessDenied() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        User agentUserA = userTestFixture.createUser("agentA@test.com", UserType.VALID_AGENT);
        User agentUserB = userTestFixture.createUser("agentB@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfileA = agentProfileTestFixture.createAgentProfile("AgentA", "AddrA",
                agentUserA.getId());
        AgentProfile agentProfileB = agentProfileTestFixture.createAgentProfile("AgentB", "AddrB",
                agentUserB.getId());

        // agentUserB가 속한 채팅방 생성
        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfileB,
                ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        SliceRequest<Long> sliceRequest = new SliceRequest<>(null, 10);

        // when & then
        // agentUserA가 agentUserB의 채팅방을 조회하려고 시도
        assertThatThrownBy(() -> chatMessageServiceFacade
                .findChatMessagesByChatRoomIdAndNoOffset(agentUserA.getEmail(), chatRoom.getId(),
                        sliceRequest))
                .isInstanceOf(ChatMessageException.class)
                .hasMessage(ResponseStatus.NOT_ALLOWED_TO_GET_CHAT_MESSAGE.getMessage());
    }
}
