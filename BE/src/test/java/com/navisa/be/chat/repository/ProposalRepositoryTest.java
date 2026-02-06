package com.navisa.be.chat.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ProposalRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private UserTestFixture userTestFixture;

    @Test
    @DisplayName("제안서가 정상적으로 저장된다")
    void saveProposal() {
        // given
        User foreignerUser = userTestFixture.createUser("f@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("a@test.com", UserType.VALID_AGENT);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());
        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                ZonedDateTime.now());

        Proposal proposal = new Proposal(chatRoom, agentProfile.getId());

        // when
        Proposal savedProposal = proposalRepository.save(proposal);

        // then
        assertThat(savedProposal.getId()).isNotNull();
        assertThat(savedProposal.getChatRoom()).isEqualTo(chatRoom);

    }
}
