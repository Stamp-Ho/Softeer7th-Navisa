package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.repository.AgentBadgeSummaryRepository;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.application.service.ApplicationFormForAgentService;
import com.navisa.be.chat.dto.response.GetChatRoomParticipantsInfoResponse;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerNationality;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerExpectedCompanyRepository;
import com.navisa.be.foreigner.repository.ForeignerNationalityRepository;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
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

    @Autowired
    private VisaApplicationFormTestFixture visaApplicationFormTestFixture;

    @Autowired
    private ProposalTestFixture proposalTestFixture;

    @Autowired
    private ApplicationFormForAgentService applicationFormForAgentService;
    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Test
    @DisplayName("채팅방에 속해 있으면 채팅방 참여자 정보를 조회할 수 있다")
    void findParticipantsInfoById_shouldSucceed_whenParticipant() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // when
        GetChatRoomParticipantsInfoResponse response = chatRoomQueryService.findParticipantsInfoById(chatRoom.getId(),
                agentUser.getEmail());

        // then
        List<AgentBadgeSummary> summaries = agentBadgeSummaryRepository.findTopKBadgeSummariesByAgentId(agentProfile.getId(), PageRequest.of(0, 2));
        ForeignerExpectedCompany expectedCompany = foreignerExpectedCompanyRepository.findByForeignerId(foreignerProfile.getId()).get();
        List<Long> nationalityIds = foreignerNationalityRepository.findByForeignerProfileId(foreignerProfile.getId())
                .stream()
                .map(ForeignerNationality::getNationality)
                .map(Nationality::getId).toList();

        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().top2BadgeIds()).containsAnyElementsOf(summaries.stream().map(AgentBadgeSummary::getBadge).map(Badge::getId).toList());
        assertThat(response.agentInfo().name()).isEqualTo(agentProfile.getName());
        assertThat(response.agentInfo().applicationFormId()).isNull();

        assertThat(response.foreignerInfo().foreignerId()).isEqualTo(foreignerProfile.getId());
        assertThat(response.foreignerInfo().expectedJob()).isEqualTo(expectedCompany.getJobTitle());
        assertThat(response.foreignerInfo().nationalityIds()).containsAnyElementsOf(nationalityIds);
        assertThat(response.foreignerInfo().isReviewRequired()).isFalse();

        assertThat(response.proposalEndRequired()).isFalse(); // 수임 제안이 없음
    }

    @Test
    @DisplayName("행정사가 내보내기 전이라면 isReviewRequired가 false고 applicationFormId가 null이다")
    void findParticipantsInfoById_shouldReturnIsReviewRequiredFalse_BeforeExport() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // 수임 중이고 신청서를 내보내기 전
        proposalTestFixture.createProposal(chatRoom, agentProfile.getId(), ProposalStatus.MATCHED);

        JobCode jobCode = agentProfileTestFixture.createJobCode("E-7", "특정활동");
        ApplicationForm applicationForm = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, false);

        // when
        GetChatRoomParticipantsInfoResponse response = chatRoomQueryService.findParticipantsInfoById(chatRoom.getId(),
                agentUser.getEmail());

        // then
        List<AgentBadgeSummary> summarys = agentBadgeSummaryRepository.findTopKBadgeSummariesByAgentId(agentProfile.getId(), PageRequest.of(0, 2));
        ForeignerExpectedCompany expectedCompany = foreignerExpectedCompanyRepository.findByForeignerId(foreignerProfile.getId()).get();
        List<Long> nationalityIds = foreignerNationalityRepository.findByForeignerProfileId(foreignerProfile.getId())
                .stream()
                .map(ForeignerNationality::getNationality)
                .map(Nationality::getId).toList();

        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().top2BadgeIds()).containsAnyElementsOf(summarys.stream().map(AgentBadgeSummary::getBadge).map(Badge::getId).toList());
        assertThat(response.agentInfo().name()).isEqualTo(agentProfile.getName());
        assertThat(response.agentInfo().applicationFormId()).isEqualTo(applicationForm.getId());

        assertThat(response.foreignerInfo().foreignerId()).isEqualTo(foreignerProfile.getId());
        assertThat(response.foreignerInfo().expectedJob()).isEqualTo(expectedCompany.getJobTitle());
        assertThat(response.foreignerInfo().nationalityIds()).containsAnyElementsOf(nationalityIds);
        assertThat(response.foreignerInfo().isReviewRequired()).isFalse();

        assertThat(response.proposalEndRequired()).isFalse(); // 내보내지 않음
    }

    @Test
    @DisplayName("행정사가 내보내기를 하면 isReviewRequired가 true가 되고 applicationFormId가 조회된다")
    void findParticipantsInfoById_shouldReturnAgentInfo_whenForeigner() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // 수임 중이고 신청서를 내보낸 후
        JobCode jobCode = agentProfileTestFixture.createJobCode("E-7", "특정활동");
        ApplicationForm applicationForm = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, false);

        proposalTestFixture.createProposal(chatRoom, agentProfile.getId(), ProposalStatus.MATCHED);

        applicationFormForAgentService.updateApplicationStatus(agentUser.getEmail(), applicationForm.getId(), true);

        // when
        GetChatRoomParticipantsInfoResponse response = chatRoomQueryService.findParticipantsInfoById(chatRoom.getId(), foreignerUser.getEmail());


        // then
        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().applicationFormId()).isEqualTo(applicationForm.getId());

        assertThat(response.foreignerInfo().foreignerId()).isEqualTo(foreignerProfile.getId());
        assertThat(response.foreignerInfo().isReviewRequired()).isTrue();

        assertThat(response.proposalEndRequired()).isFalse(); // 내보낸지 2주가 안 지남
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

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // when & then
        assertThatThrownBy(() -> chatRoomQueryService.findParticipantsInfoById(chatRoom.getId(), nonParticipant.getEmail()))
                .isInstanceOf(ChatRoomException.class)
                .hasMessage(ResponseStatus.NOT_ALLOWED_TO_ACCESS_CHATROOM.getMessage());
    }

    @Test
    @DisplayName("행정사가 수임 완료를 3일동안 입력하지 않으면 proposalEndRequired가 true로 반환된다")
    void findParticipantsInfoById_shouldReturnProposalEndRequiredTrue_whenAgentReplyNotExistAfter3DaysAfterMailSent() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent", "Addr", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        ChatRoom chatRoom = chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // 수임 중이고 신청서를 내보낸 후
        JobCode jobCode = agentProfileTestFixture.createJobCode("E-7", "특정활동");
        ApplicationForm applicationForm = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, true);
        ReflectionTestUtils.setField(applicationForm, "exportedAt", LocalDateTime.now().minusDays(14));
        applicationFormRepository.saveAndFlush(applicationForm);

        proposalTestFixture.createProposal(chatRoom, agentProfile.getId(), ProposalStatus.MATCHED);

        // 비자신청서를 내보낸지 2주가 흐름
        applicationFormForAgentService.updateApplicationStatus(agentUser.getEmail(), applicationForm.getId(), true);

        // when
        GetChatRoomParticipantsInfoResponse response = chatRoomQueryService.findParticipantsInfoById(chatRoom.getId(),
                foreignerUser.getEmail());

        // then
        assertThat(response.agentInfo().agentId()).isEqualTo(agentProfile.getId());
        assertThat(response.agentInfo().applicationFormId()).isEqualTo(applicationForm.getId());

        assertThat(response.foreignerInfo().foreignerId()).isEqualTo(foreignerProfile.getId());
        assertThat(response.foreignerInfo().isReviewRequired()).isTrue();

        assertThat(response.proposalEndRequired()).isTrue();
    }
}