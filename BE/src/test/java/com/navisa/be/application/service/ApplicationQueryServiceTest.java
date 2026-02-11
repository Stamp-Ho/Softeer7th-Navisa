package com.navisa.be.application.service;

import com.navisa.be.application.dto.response.VisaApplicationCardResponse;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
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
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.VisaApplicationFormTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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
    private VisaApplicationFormTestFixture visaApplicationFormFixture;

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

    private AgentProfile defaultAgent;
    private JobCode defaultJobCode;
    private final String defaultAgentEmail = "agent@test.com";

    @BeforeEach
    void setUp() {
        User user = saveUser(defaultAgentEmail, UserType.VALID_AGENT);
        defaultAgent = saveAgentProfile(user.getId());
        defaultJobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));
    }

    @DisplayName("승인된 행정사는 자신이 최근에 수정한 비자 신청서 목록을 최대 6개까지 조회할 수 있다.")
    @Test
    void getRecentVisaForms_Success() {
        // given
        for (int i = 1; i <= 7; i++) {
            ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(UUID.randomUUID(), null));
            VisaApplicationForm form = visaApplicationFormFixture.createVisaApplicationForm(
                    defaultAgent, foreigner, defaultJobCode, false, 10 + i, null);

            ReflectionTestUtils.setField(form, "updatedAt", LocalDateTime.now().minusDays(10 - i));

            em.flush();
        }
        em.clear();

        // when
        List<RecentVisaFormsResponse> result = visaApplicationQueryService.getRecentVisaForms(defaultAgentEmail);

        // then
        assertThat(result).hasSize(6);
        assertThat(result.get(0).currentStep()).isEqualTo(17); // 가장 최근 (i=7)
        assertThat(result.get(1).currentStep()).isEqualTo(16); // 두 번째 (i=6)
        assertThat(result.get(0).lastModifiedAt()).isNotNull();
    }

    @DisplayName("등록된 서류가 6개 미만(예: 2개)일 경우, 존재하는 2개만 반환한다.")
    @Test
    void getRecentVisaForms_LessThanSix() {
        // given
        for (int i = 1; i <= 2; i++) {
            ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(UUID.randomUUID(), null));
            visaApplicationFormFixture.createVisaApplicationForm(
                    defaultAgent, foreigner, defaultJobCode, false, 50 + i, null);
        }

        // when
        List<RecentVisaFormsResponse> result = visaApplicationQueryService.getRecentVisaForms(defaultAgentEmail);

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

        String profileImageKey = "visa/photos/my-photo.png";

        // 두 개의 신청서 생성 (생성 시간을 다르게 하여 최신순 확인)
        VisaApplicationForm oldForm = visaApplicationFormFixture.createVisaApplicationForm(
                defaultAgent, foreigner, defaultJobCode, false, 50, null);
        VisaApplicationForm latestForm = visaApplicationFormFixture.createVisaApplicationForm(
                defaultAgent, foreigner, defaultJobCode, true, 150, profileImageKey);

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
        User foreignerUser = saveUser("foreigner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(foreignerUser.getId(), ForeignerSearchStatus.REQUESTING));

        VisaApplicationForm form = visaApplicationFormFixture.createVisaApplicationForm(
                defaultAgent, foreigner, defaultJobCode, false, 80, null);

        em.flush();
        em.clear();

        // when
        VisaApplicationDetailResponse result = visaApplicationQueryService.getVisaFormForAgent(defaultAgentEmail, form.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.applicationFormId()).isEqualTo(form.getId());
        assertThat(result.filledCount()).isEqualTo(80);
    }

    @DisplayName("다른 행정사가 담당하는 비자 신청서를 조회하려고 하면 FORBIDDEN 예외가 발생한다.")
    @Test
    void getVisaFormForAgent_Forbidden_Fail() {
        // given
        // 1번 행정사 (조회 시도자) - defaultAgent 사용
        // 2번 행정사 (실제 담당자)
        User ownerUser = saveUser("owner@test.com", UserType.VALID_AGENT);
        AgentProfile ownerAgent = saveAgentProfile(ownerUser.getId());

        User foreignerUser = saveUser("foreigner2@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(foreignerUser.getId(), ForeignerSearchStatus.REQUESTING));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "F2", "거주", null, null));

        // 2번 행정사의 신청서 생성
        VisaApplicationForm form = visaApplicationFormFixture.createVisaApplicationForm(
                ownerAgent, foreigner, jobCode, false, 10, null);

        em.flush();
        em.clear();

        // when & then
        assertThatThrownBy(() -> visaApplicationQueryService.getVisaFormForAgent(defaultAgentEmail, form.getId()))
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

    @DisplayName("행정사는 자신의 비자 신청서 목록을 필터링하여 조회할 수 있다.")
    @Test
    void findVisaFormsByFilter_Success() {
        // given
        // 10개의 신청서 생성 (5개 완료, 5개 미완료)
        for (int i = 1; i <= 10; i++) {
            ForeignerProfile foreigner = foreignerProfileRepository
                    .save(new ForeignerProfile(UUID.randomUUID(), null));
            boolean isDone = (i % 2 == 0);

            VisaApplicationForm form = visaApplicationFormFixture.createVisaApplicationForm(
                    defaultAgent, foreigner, defaultJobCode, isDone, 10 + i,
                    "visa/profile/dummy.png");

            em.flush();

            // 정렬 순서 보장을 위해 Native Query로 시간 강제 업데이트 (JPA Auditing 우회)
            LocalDateTime time = LocalDateTime.now().minusMinutes(100 - i * 10);
            em.createNativeQuery("UPDATE visa_application_form SET created_at = :time, updated_at = :time WHERE application_form_id = :id")
                    .setParameter("time", time)
                    .setParameter("id", form.getId())
                    .executeUpdate();
        }

        // 다른 행정사의 신청서 (조회되면 안됨)
        User otherUser = saveUser("other_agent@test.com", UserType.VALID_AGENT);
        AgentProfile otherAgent = saveAgentProfile(otherUser.getId());
        ForeignerProfile otherForeigner = foreignerProfileRepository
                .save(new ForeignerProfile(UUID.randomUUID(), null));

        // 필터링 제외 확인용 (완료 상태, 다른 행정사)
        visaApplicationFormFixture.createVisaApplicationForm(
                otherAgent, otherForeigner, defaultJobCode, true, 100, "visa/profile/other.png");

        em.flush();
        em.clear();

        // when
        // 완료된 신청서만 조회 & No-Offset 첫 페이지 (3개)
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 3);
        SliceResponse<VisaApplicationCardResponse, UUID> response = visaApplicationQueryService
                .findVisaFormsByFilter(defaultAgentEmail, sliceRequest, true); // 완료된 것만 조회

        // then
        assertThat(response.content()).hasSize(3);
        assertThat(response.existsNext()).isTrue();
        // 최신순 (i=10(완료), i=8(완료), i=6(완료))
        assertThat(response.content().get(0).currentStep()).isEqualTo(10 + 10);
        assertThat(response.content().get(1).currentStep()).isEqualTo(10 + 8);
        assertThat(response.content().get(2).currentStep()).isEqualTo(10 + 6);
    }

    @DisplayName("행정사는 자신에게 배정되지 않은 비자 신청서를 목록 조회에서 볼 수 없다.")
    @Test
    void findVisaFormsByFilter_AccessControl() {
        // given
        // defaultAgent는 신청서가 없음

        // 다른 행정사의 신청서 생성
        User otherUser = saveUser("other_access@test.com", UserType.VALID_AGENT);
        AgentProfile otherAgent = saveAgentProfile(otherUser.getId());
        ForeignerProfile otherForeigner = foreignerProfileRepository
                .save(new ForeignerProfile(UUID.randomUUID(), null));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));

        visaApplicationFormRepository
                .save(new VisaApplicationForm(otherAgent, otherForeigner, jobCode, false, 150, 50));

        em.flush();
        em.clear();

        // when
        SliceRequest<UUID> sliceRequest = new SliceRequest<>(null, 10);
        SliceResponse<VisaApplicationCardResponse, UUID> response = visaApplicationQueryService
                .findVisaFormsByFilter(defaultAgentEmail, sliceRequest, null); // 필터 없음

        // then
        assertThat(response.content()).isEmpty();
    }
}