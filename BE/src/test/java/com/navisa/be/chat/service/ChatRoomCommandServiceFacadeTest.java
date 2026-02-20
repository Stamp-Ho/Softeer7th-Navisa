package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.ChatRoomTestFixture;
import com.navisa.be.support.ForeignerProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.ProposalTestFixture;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.support.VisaApplicationFormTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ChatRoomCommandServiceFacadeTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomServiceFacade chatRoomServiceFacade;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private VisaApplicationFormTestFixture visaApplicationFormTestFixture;

    @Autowired
    private ProposalTestFixture proposalTestFixture;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Test
    @DisplayName("채팅방 차단 시 채팅방 상태가 BLOCKED로 변경되고 제안이 REJECTED되며 비자신청서의 담당 행정사가 해제된다")
    void blockChatRoom_Success() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Address",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        Proposal proposal = proposalTestFixture.createProposal(chatRoom, agentProfile.getId(),
                ProposalStatus.PROPOSED);

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));

        ApplicationForm applicationForm = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile,
                foreignerProfile, jobCode, false);

        // when
        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "BLOCK_MESSAGE",
                MessageType.CHATROOM_BLOCKED);
        chatRoomServiceFacade.updateBlockStatusToEntity("foreigner@test.com", chatRoom.getId(), request);

        // then
        assertThat(chatRoom.getStatus()).isEqualTo(ChatRoomStatus.BLOCKED);

        Proposal updatedProposal = proposalRepository.findById(proposal.getId()).orElseThrow();
        assertThat(updatedProposal.getStatus()).isEqualTo(ProposalStatus.REJECTED);

        ApplicationForm updatedForm = applicationFormRepository.findById(applicationForm.getId()).orElseThrow();
        assertThat(updatedForm.getAgentProfile()).isNull();
    }

    @Test
    @DisplayName("제안이나 비자신청서가 없는 채팅방 차단 시에도 채팅방 상태는 BLOCKED로 정상 변경된다")
    void blockChatRoom_Success_NoProposalOrVisaForm() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner2@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent2@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent2", "Address",
                agentUser.getId());

        // 채팅방만 생성하고 제안이나 신청서는 생성하지 않음
        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // when
        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "BLOCK_MESSAGE",
                MessageType.CHATROOM_BLOCKED);
        chatRoomServiceFacade.updateBlockStatusToEntity("foreigner2@test.com", chatRoom.getId(), request);

        // then
        assertThat(chatRoom.getStatus()).isEqualTo(ChatRoomStatus.BLOCKED);
    }

    @Test
    @DisplayName("채팅방 참여자가 아닌 유저가 차단을 시도하면 예외가 발생한다")
    void blockChatRoom_NotParticipant_Fail() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner_owner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        User agentUser = userTestFixture.createUser("agent_owner@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("AgentOwner", "Address",
                agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // 다른 유저 생성 (참여자 아님), 프로필도 생성해야 함(Validation 로직에서 조회하므로)
        User otherUser = userTestFixture.createUser("other_foreigner@test.com", UserType.FILLED_FOREIGNER);
        foreignerProfileTestFixture.createForeignerProfile(otherUser);

        // when & then
        ChatMessageRequest request = new ChatMessageRequest(
                chatRoom.getId(),
                UUID.randomUUID(),
                "BLOCK_MESSAGE",
                MessageType.CHATROOM_BLOCKED);
        assertThatThrownBy(
                () -> chatRoomServiceFacade.updateBlockStatusToEntity("other_foreigner@test.com", chatRoom.getId(), request))
                .isInstanceOf(ChatRoomException.class)
                .hasMessageContaining(
                        ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM.getMessage());
    }
}
