package com.navisa.be.foreigner.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.common.repository.NationalityRepository;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerCardResponse;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.foreigner.repository.ForeignerSimilarityRepository;
import com.navisa.be.support.ForeignerFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.repository.ForeignerExpectedCompanyRepository;

@Transactional
class ForeignerQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private ForeignerQueryService foreignerQueryService;

    @Autowired
    private ForeignerCommandService foreignerCommandService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private NationalityRepository nationalityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ForeignerSimilarityRepository foreignerSimilarityRepository;

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private AgentSpecializedJobRepository agentSpecializedJobRepository;

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;

    @Test
    @DisplayName("userId로 외국인 전체 정보를 조회한다")
    void findForeignerTotalInfo() {
        // given
        Language language = languageRepository.save(new Language(null, "English"));
        Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));
        User savedUser = userRepository.save(User.createGoogleUser("query@example.com", UserType.UNFILLED_FOREIGNER));
        UUID userId = savedUser.getId();
        boolean isWork = false;

        ForeignerRegisterRequest request = ForeignerFixture.createForeignerRegisterRequest(
                List.of(nationality.getId()),
                List.of(language.getId()),
                isWork);

        // Save data using Command Service
        foreignerCommandService.registerForeignerTotalInfo(request, userId);

        em.flush();
        em.clear();

        // when
        ForeignerQueryResponse response = foreignerQueryService.findForeignerTotalInfo(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isIdle()).isEqualTo(request.isIdle());

        assertThat(response.education()).usingRecursiveComparison().isEqualTo(request.education());

        assertThat(response.expectedCompany()).usingRecursiveComparison().isEqualTo(request.expectedCompany());

        assertThat(response.foreignerCareers()).hasSize(1);
        assertThat(response.foreignerCareers().get(0)).usingRecursiveComparison()
                .isEqualTo(request.foreignerCareers().get(0));

        assertThat(response.languageIdList()).containsExactly(language.getId());
        assertThat(response.nationIdList()).containsExactly(nationality.getId());
    }

    @Test
    @DisplayName("이메일로 외국인 상세 요건 입력 상태를 조회한다.")
    void checkForeignerFilledStatus() {
        // given
        Language language = languageRepository.save(new Language(null, "Korean"));
        Nationality nationality = nationalityRepository.save(new Nationality(null, "Vietnam"));

        String email = "test@navisa.com";
        User user = userRepository.save(User.createGoogleUser(email, UserType.FILLED_FOREIGNER));

        UUID userId = user.getId(); // 저장된 유저의 ID 추출

        // 2. 해당 userId를 사용하여 외국인 프로필 등록
        ForeignerRegisterRequest request = ForeignerFixture.createForeignerRegisterRequest(
                List.of(nationality.getId()),
                List.of(language.getId()),
                true);

        foreignerCommandService.registerForeignerTotalInfo(request, userId);

        em.flush();
        em.clear();

        // when
        ForeignerStatusResponse response = foreignerQueryService.checkForeignerFilledStatus(email);

        // then
        assertThat(response).isNotNull();
        assertThat(response.foreignerProfileId()).isNotNull();
        assertThat(response.isCompletedRecommendation()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 상태 조회 시 예외가 발생한다.")
    void checkForeignerFilledStatus_UserNotFound() {
        // given
        String nonExistentEmail = "none@navisa.com";

        // when & then
        assertThatThrownBy(() -> foreignerQueryService.checkForeignerFilledStatus(nonExistentEmail))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("status", ResponseStatus.USER_INVALID);
    }

    @Test
    @DisplayName("행정사 전문분야와 겹치는 외국인을 추천한다")
    void findForeignerCardMatchOnSpecializedJob_integration() {
        // given
        // 1. Agent Setup
        String email = "agent@test.com";
        User agentUser = userRepository.save(new User(email, "password", UserType.VALID_AGENT,
                LoginType.EMAIL, true));

        AgentProfile agentProfile = new AgentProfile(
                "Agent Name", LocalDate.now(), "url", "09:00", "Office", "Addr", "Detail", "Hist",
                agentUser.getId(), "Lic", LocalDate.now(), "Page", "Mgmt", "Comment");
        agentProfileRepository.save(agentProfile);

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "JC001", "Visa", new float[512]));

        AgentSpecializedJob specializedJob = new AgentSpecializedJob(agentProfile, jobCode);
        agentSpecializedJobRepository.save(specializedJob);

        // 2. Foreigner Setup (Matching)
        ForeignerProfile matchForeigner = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(matchForeigner);

        ForeignerSimilarity similarity = new ForeignerSimilarity(
                null,
                matchForeigner.getId(),
                new double[] {},
                new long[] { jobCode.getId() } // 매칭시킬 Agent의 JobCode
        );
        foreignerSimilarityRepository.save(similarity);

        ForeignerExpectedCompany expectedCompany = new ForeignerExpectedCompany(
                null, matchForeigner.getId(), "Samsung", "Software Engineer", LocalDate.now());
        foreignerExpectedCompanyRepository.save(expectedCompany);

        // 3. Foreigner Setup (Not Matching)
        ForeignerProfile otherForeigner = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(otherForeigner);

        ForeignerSimilarity otherSimilarity = new ForeignerSimilarity(
                null,
                otherForeigner.getId(),
                new double[] {},
                new long[] { 9999L });
        foreignerSimilarityRepository.save(otherSimilarity);

        em.flush();
        em.clear();

        // when
        List<ForeignerCardResponse> result = foreignerQueryService
                .findForeignerCardMatchOnSpecializedJob(email);

        // then
        assertThat(result).hasSize(1);
        ForeignerCardResponse response = result.get(0);
        assertThat(response.foreignerId()).isEqualTo(matchForeigner.getId());
        assertThat(response.jobTitle()).isEqualTo("Software Engineer");
    }

    @Test
    @DisplayName("매칭되는 외국인이 없으면 빈 리스트를 반환한다")
    void findForeignerCardMatchOnSpecializedJob_shouldReturnEmptyList_whenNoMatch() {
        // given
        // 1. Agent Setup
        String email = "agent_empty@test.com";
        User agentUser = userRepository.save(new User(email, "password", UserType.VALID_AGENT,
                LoginType.EMAIL, true));

        AgentProfile agentProfile = new AgentProfile(
                "Agent Empty", LocalDate.now(), "url", "09:00", "Office", "Addr", "Detail", "Hist",
                agentUser.getId(), "Lic", LocalDate.now(), "Page", "Mgmt", "Comment");
        agentProfileRepository.save(agentProfile);

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "JC002", "Visa2", new float[512]));

        AgentSpecializedJob specializedJob = new AgentSpecializedJob(agentProfile, jobCode);
        agentSpecializedJobRepository.save(specializedJob);

        // 2. Foreigner Setup (Not Matching)
        ForeignerProfile otherForeigner = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.IDLE);
        foreignerProfileRepository.save(otherForeigner);

        ForeignerSimilarity otherSimilarity = new ForeignerSimilarity(
                null,
                otherForeigner.getId(),
                new double[] {},
                new long[] { 9999L }); // Not matching JC002
        foreignerSimilarityRepository.save(otherSimilarity);

        em.flush();
        em.clear();

        // when
        List<ForeignerCardResponse> result = foreignerQueryService.findForeignerCardMatchOnSpecializedJob(email);

        // then
        assertThat(result).isEmpty();
    }
}
