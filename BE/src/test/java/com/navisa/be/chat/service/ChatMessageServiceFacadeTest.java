package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.response.ChatMessageCountResponse;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ProposalRepository;
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

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    @DisplayName("Foreigner 이메일로 안 읽은 메시지 수를 조회한다")
    void findNonReadCountByUserEmail_Foreigner() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("a@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
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
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        // Foreigner가 보낸 메시지 3개 (안 읽음)
        chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "Msg1", false);
        chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "Msg2", false);
        chatRoomTestFixture.createChatMessage(chatRoom, foreignerProfile.getId(), "Msg3", false);

        // when
        ChatMessageCountResponse response = chatMessageServiceFacade.findNonReadCountByUserEmail(agentUser.getEmail());

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
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());

        // 매칭된 방 (메시지 있음)
        ChatRoom chatRoom1 = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());
        Proposal proposal1 = new Proposal(chatRoom1, agentProfile.getId());
        ReflectionTestUtils.setField(proposal1, "status", ProposalStatus.MATCHED);
        proposalRepository.save(proposal1);

        chatRoomTestFixture.createChatMessage(chatRoom1, foreignerProfile.getId(), "Hello", false);

        // 매칭 안된 방 (메시지 없음)
        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // when
        ChatMessageCountResponse response = chatMessageServiceFacade.findMatchedNonReadCountByUserEmail(agentUser.getEmail());

        // then
        assertThat(response.count()).isEqualTo(1L);
    }
}
