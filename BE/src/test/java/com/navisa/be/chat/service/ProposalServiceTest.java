package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class ProposalServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProposalService proposalService;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @MockitoBean
    private ChatServiceFacade chatServiceFacade;

    @Test
    @DisplayName("제안 생성 시 Redis 발행 이벤트에 User ID가 올바르게 전달되는지 검증")
    void createProposal_Success() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner_prop@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_prop@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("AgentProp", "Address",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "제안합니다.",
                MessageType.PROPOSAL,
                ZonedDateTime.now());

        // when
        // 행정사가 제안 생성
        proposalService.createProposal(agentUser.getEmail(), chatRoom.getId(), request);

        // then
        // 1. DB에 제안 생성 확인
        Proposal proposal = proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom).orElseThrow();
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.PROPOSED);
        assertThat(proposal.getSenderId()).isEqualTo(agentProfile.getId()); // Sender ID는 Profile ID

        // 2. ChatServiceFacade 호출 시 User ID가 전달되었는지 확인 (중요)
        verify(chatServiceFacade).saveAndPublishMessage(eq(agentUser.getId()), any(ChatMessageRequest.class),
                any(ChatRoom.class));
    }

    @Test
    @DisplayName("제안 성사(MATCHED) 시 상태 변경 및 Redis 발행 검증")
    void updateProposalStatusMatched_Success() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner_match@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_match@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("AgentMatch", "Address",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());
        proposalRepository.save(new Proposal(chatRoom, agentProfile.getId())); // 초기 상태 PROPOSED

        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "수락합니다.",
                MessageType.ACCEPTED,
                ZonedDateTime.now());

        // when
        // 외국인이 수락 (MATCHED)
        proposalService.updateProposalStatusMatched(foreignerUser.getEmail(), chatRoom.getId(), request);

        // then
        Proposal proposal = proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom).orElseThrow();
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.MATCHED);

        verify(chatServiceFacade).saveAndPublishMessage(eq(foreignerUser.getId()), any(ChatMessageRequest.class),
                any(ChatRoom.class));
    }

    @Test
    @DisplayName("제안 거절(REJECTED) 시 상태 변경 및 Redis 발행 검증")
    void updateProposalStatusRejected_Success() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner_reject@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_reject@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("AgentReject", "Address",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());
        proposalRepository.save(new Proposal(chatRoom, agentProfile.getId()));

        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "거절합니다.",
                MessageType.REJECTED,
                ZonedDateTime.now());

        // when
        // 외국인이 거절 (REJECTED)
        proposalService.updateProposalStatusRejected(foreignerUser.getEmail(), chatRoom.getId(), request);

        // then
        Proposal proposal = proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom).orElseThrow();
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.REJECTED);

        verify(chatServiceFacade).saveAndPublishMessage(eq(foreignerUser.getId()), any(ChatMessageRequest.class),
                any(ChatRoom.class));
    }

    @Test
    @DisplayName("제안 취소(CANCELED) 시 상태 변경 및 Redis 발행 검증")
    void updateProposalStatusCanceled_Success() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner_cancel@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_cancel@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("AgentCancel", "Address",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());
        Proposal proposal = new Proposal(chatRoom, agentProfile.getId());
        ReflectionTestUtils.setField(proposal,"status", ProposalStatus.MATCHED);
        proposalRepository.save(proposal);

        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "취소합니다.",
                MessageType.CANCELED,
                ZonedDateTime.now());

        // when
        // 행정사가 취소 (CANCELED)
        proposalService.updateProposalStatusCanceled(agentUser.getEmail(), chatRoom.getId(), request);

        // then
        Proposal findProposal = proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom).orElseThrow();
        assertThat(findProposal.getStatus()).isEqualTo(ProposalStatus.CANCELED);

        verify(chatServiceFacade).saveAndPublishMessage(eq(agentUser.getId()), any(ChatMessageRequest.class),
                any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅방 참여자가 아닌 유저가 제안 관련 작업을 시도하면 예외 발생")
    void proposalAction_NotParticipant_Fail() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner_fail@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_fail@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("AgentFail", "Address",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        User otherUser = userTestFixture.createUser("other_agent@test.com", UserType.VALID_AGENT);
        agentProfileTestFixture.createAgentProfile("OtherAgent", "Address", otherUser.getId());

        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "침입 시도",
                MessageType.PROPOSAL,
                ZonedDateTime.now());

        // when & then
        assertThatThrownBy(() -> proposalService.createProposal(otherUser.getEmail(), chatRoom.getId(), request))
                .isInstanceOf(ChatRoomException.class)
                .hasMessageContaining(ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM.getMessage());
    }
}
