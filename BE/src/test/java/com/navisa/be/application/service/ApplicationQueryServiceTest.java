package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.dto.response.VisaApplicationDetailResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.application.service.ApplicationQueryService;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ApplicationQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationQueryService visaApplicationQueryService;

    @Autowired
    private ApplicationFormRepository visaApplicationFormRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("승인된 행정사는 자신이 최근에 수정한 비자 신청서 목록을 최대 6개까지 조회할 수 있다.")
    @Test
    void getRecentVisaForms_Success() {
        // given
        String email = "agent@test.com";
        User user = saveUser(email, UserType.VALID_AGENT);
        AgentProfile agent = saveAgentProfile(user.getId());
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "V01", "관광비자", null, null));

        for (int i = 1; i <= 7; i++) {
            ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(UUID.randomUUID(), null));
            VisaApplicationForm form = new VisaApplicationForm(agent, foreigner, jobCode, false, 150, 10 + i);
            visaApplicationFormRepository.save(form);

            ReflectionTestUtils.setField(form, "updatedAt", LocalDateTime.now().minusDays(10 - i));

            em.flush();
        }
        em.clear();

        // when
        List<RecentVisaFormsResponse> result = visaApplicationQueryService.getRecentVisaForms(email);

        // then
        assertThat(result).hasSize(6);
        assertThat(result.get(0).currentStep()).isEqualTo(17); // i=7 (10+7)
        assertThat(result.get(1).currentStep()).isEqualTo(16); // i=6 (10+6)
        assertThat(result.get(0).lastModifiedAt()).isNotNull();
    }

    @DisplayName("등록된 서류가 6개 미만(예: 2개)일 경우, 존재하는 2개만 반환한다.")
    @Test
    void getRecentVisaForms_LessThanSix() {
        // given
        String email = "agent@test.com";
        User user = saveUser(email, UserType.VALID_AGENT);
        AgentProfile agent = saveAgentProfile(user.getId());
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "V01", "관광비자", null, null));

        for (int i = 1; i <= 2; i++) {
            ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(UUID.randomUUID(), null));
            visaApplicationFormRepository.save(new VisaApplicationForm(agent, foreigner, jobCode, false, 150, 50 + i));
        }

        // when
        List<RecentVisaFormsResponse> result = visaApplicationQueryService.getRecentVisaForms(email);

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("UserType이 VALID_AGENT가 아닌 경우 AGENT_NOT_APPROVED 예외가 발생한다.")
    @Test
    void getRecentVisaForms_NotApproved_Fail() {
        // given
        String email = "pending_agent@test.com";
        saveUser(email, UserType.INVALID_AGENT);

        // when & then
        assertThatThrownBy(() -> visaApplicationQueryService.getRecentVisaForms(email))
                .isInstanceOf(ApplicationException.class);
    }

    @DisplayName("로그인한 외국인은 자신이 가장 최근에 생성한 비자 신청서의 상세 정보를 조회할 수 있다.")
    @Test
    void getLatestVisaFormForForeigner_Success() {
        // given
        String email = "foreigner@test.com";
        User user = saveUser(email, UserType.FILLED_FOREIGNER);

        ForeignerProfile foreigner = new ForeignerProfile(user.getId(), ForeignerSearchStatus.REQUESTING);
        foreignerProfileRepository.save(foreigner);

        User agentUser = saveUser("agent_dummy@test.com", UserType.VALID_AGENT);
        AgentProfile agent = saveAgentProfile(agentUser.getId());

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));

        // 두 개의 신청서 생성 (생성 시간을 다르게 하여 최신순 확인)
        String profileImageKey = "visa/photos/my-photo.png";
        VisaApplicationForm oldForm = new VisaApplicationForm(agent, foreigner, jobCode, false, 150, 50);
        VisaApplicationForm latestForm = new VisaApplicationForm(agent, foreigner, jobCode, true, 150, 150);

        ReflectionTestUtils.setField(latestForm, "profileObjectKey", profileImageKey);

        visaApplicationFormRepository.save(oldForm);
        visaApplicationFormRepository.save(latestForm);

        ReflectionTestUtils.setField(oldForm, "createdAt", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(latestForm, "createdAt", LocalDateTime.now());

        em.flush();
        em.clear();

        // when
        VisaApplicationDetailResponse result = visaApplicationQueryService.getLatestVisaFormForForeigner(email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.totalCount()).isEqualTo(150);
        assertThat(result.filledCount()).isEqualTo(150);
        assertThat(result.foreignerProfileImgUrl()).isEqualTo(profileImageKey);
        assertThat(result.isDone()).isTrue();
        assertThat(result.sections()).hasSize(9);
    }

    @DisplayName("외국인의 비자 신청서가 하나도 없을 경우 VISA_FORM_NOT_FOUND 예외가 발생한다.")
    @Test
    void getLatestVisaFormForForeigner_NotFound_Fail() {
        // given
        String email = "new_foreigner@test.com";
        User user = saveUser(email, UserType.FILLED_FOREIGNER);
        foreignerProfileRepository.save(new ForeignerProfile(user.getId(), null));

        // when & then
        assertThatThrownBy(() -> visaApplicationQueryService.getLatestVisaFormForForeigner(email))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining(ResponseStatus.VISA_APP_FORM_NOT_FOUND.getMessage());
    }

    @DisplayName("행정사는 자신에게 배정된 특정 비자 신청서를 상세 조회할 수 있다.")
    @Test
    void getVisaFormForAgent_Success() {
        // given
        String agentEmail = "agent@test.com";
        User agentUser = saveUser(agentEmail, UserType.VALID_AGENT);
        AgentProfile agent = saveAgentProfile(agentUser.getId());

        User foreignerUser = saveUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(foreignerUser.getId(), ForeignerSearchStatus.REQUESTING));

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));

        // 행정사가 담당자로 지정된 신청서 생성
        VisaApplicationForm form = visaApplicationFormRepository.save(
                new VisaApplicationForm(agent, foreigner, jobCode, false, 150, 80)
        );

        em.flush();
        em.clear();

        // when
        VisaApplicationDetailResponse result = visaApplicationQueryService.getVisaFormForAgent(agentEmail, form.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.applicationFormId()).isEqualTo(form.getId());
        assertThat(result.filledCount()).isEqualTo(80);
    }

    @DisplayName("다른 행정사가 담당하는 비자 신청서를 조회하려고 하면 FORBIDDEN 예외가 발생한다.")
    @Test
    void getVisaFormForAgent_Forbidden_Fail() {
        // given
        // 1번 행정사 (조회 시도자)
        String readerEmail = "reader@test.com";
        User readerUser = saveUser(readerEmail, UserType.VALID_AGENT);
        saveAgentProfile(readerUser.getId());

        // 2번 행정사 (실제 담당자)
        User ownerUser = saveUser("owner@test.com", UserType.VALID_AGENT);
        AgentProfile ownerAgent = saveAgentProfile(ownerUser.getId());

        User foreignerUser = saveUser("foreigner2@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(foreignerUser.getId(), ForeignerSearchStatus.REQUESTING));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "F2", "거주", null, null));

        // 2번 행정사의 신청서 생성
        VisaApplicationForm form = visaApplicationFormRepository.save(
                new VisaApplicationForm(ownerAgent, foreigner, jobCode, false, 150, 10)
        );

        em.flush();
        em.clear();

        // when & then
        assertThatThrownBy(() -> visaApplicationQueryService.getVisaFormForAgent(readerEmail, form.getId()))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining(ResponseStatus.FORBIDDEN.getMessage());
    }

    private User saveUser(String email, UserType type) {
        return userRepository.save(new User(
                email,
                "password123",
                type,
                LoginType.EMAIL,
                true));
    }

    private AgentProfile saveAgentProfile(UUID userId) {
        return agentProfileRepository.save(new AgentProfile(
                "김행정", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", userId, "LIC123", LocalDate.now(), "P123", "M123", "코멘트"));
    }
}