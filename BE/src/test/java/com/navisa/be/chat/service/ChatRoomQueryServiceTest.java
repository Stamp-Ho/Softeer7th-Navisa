package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.repository.AgentBadgeSummaryRepository;
import com.navisa.be.chat.dto.response.GetChatRoomParticipantsInfoResponse;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerNationality;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerExpectedCompanyRepository;
import com.navisa.be.foreigner.repository.ForeignerNationalityRepository;
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
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChatRoomQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatRoomQueryService chatRoomQueryService;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;

    @Autowired
    private ForeignerNationalityRepository foreignerNationalityRepository;
    @Autowired
    private AgentBadgeSummaryRepository agentBadgeSummaryRepository;

    @Test
    @DisplayName("행정사는 채팅방이 있으면 외국인의 정보를 조회할 수 있다")
    void findParticipantsInfoById_shouldReturnForeignerInfo_whenAgent() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // when
        GetChatRoomParticipantsInfoResponse response = chatRoomQueryService.findParticipantsInfoById(chatRoom.getId(), agentUser.getEmail());

        // then
        assertThat(response.agentInfo()).isNull();

        ForeignerExpectedCompany expectedCompany = foreignerExpectedCompanyRepository.findByForeignerId(foreignerProfile.getId()).get();
        List<Long> nationalityIds = foreignerNationalityRepository.findByForeignerProfileId(foreignerProfile.getId()).stream()
                .map(ForeignerNationality::getNationality)
                .map(Nationality::getId).toList();
        assertThat(response.foreignerInfo().foreignerId()).isEqualTo(foreignerProfile.getId());
        assertThat(response.foreignerInfo().expectedJob()).isEqualTo(expectedCompany.getJobTitle());
        assertThat(response.foreignerInfo().nationalityIds()).containsAnyElementsOf(nationalityIds);
    }

    @Test
    @DisplayName("외국인은 채팅방이 있으면 행정사 정보를 조회할 수 있다")
    void findParticipantsInfoById_shouldReturnAgentInfo_whenForeigner() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // when
        GetChatRoomParticipantsInfoResponse response = chatRoomQueryService.findParticipantsInfoById(chatRoom.getId(), foreignerUser.getEmail());

        // then
        List<AgentBadgeSummary> summarys = agentBadgeSummaryRepository.findTopKBadgeSummarysByAgentId(agentProfile.getId(), PageRequest.of(0, 2));
        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().top2BadgeIds()).containsAnyElementsOf(summarys.stream()
                .map(AgentBadgeSummary::getBadge).map(Badge::getId).toList());
        assertThat(response.agentInfo().name()).isEqualTo(agentProfile.getName());
    }

    @Test
    @DisplayName("행정사와 외국인 사이에 채팅방이 없으면 채팅방 정보 조회에서 예외가 발생한다")
    void findParticipantsInfoById_shouldThrowException_whenNoRoom() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());

        // when & then
        assertThatThrownBy(() -> chatRoomQueryService.findParticipantsInfoById(1L, agentUser.getEmail()))
                .isInstanceOf(ChatRoomException.class)
                .hasMessage(ResponseStatus.NOT_FOUND_CHATROOM.getMessage());
    }

    @Test
    @DisplayName("자신이 참여하지 않는 채팅방 정보를 조회하면 예외가 발생한다")
    void findParticipantsInfoById_shouldThrowException_whenNotParticipant() {
        // given
        User nonParticipant = userTestFixture.createUser("agent1@test.com", UserType.VALID_AGENT);
        agentProfileTestFixture.createAgentProfile("Agent1", "Addr1", nonParticipant.getId());

        User agentUser = userTestFixture.createUser("agent2@test.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent2", "Addr2", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT, ZonedDateTime.now());

        // when & then
        assertThatThrownBy(() -> chatRoomQueryService.findParticipantsInfoById(chatRoom.getId(), nonParticipant.getEmail()))
                .isInstanceOf(ChatRoomException.class)
                .hasMessage(ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM.getMessage());
    }
}