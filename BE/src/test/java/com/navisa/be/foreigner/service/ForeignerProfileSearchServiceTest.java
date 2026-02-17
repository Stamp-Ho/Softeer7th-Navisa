package com.navisa.be.foreigner.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.foreigner.dto.request.ForeignerCardRequest;
import com.navisa.be.foreigner.dto.response.ForeignerCardExtensionResponse;
import com.navisa.be.foreigner.dto.response.ForeignerCardResponse;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.*;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.JobGroup;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class ForeignerProfileSearchServiceTest extends IntegrationTestSupport {

    @Autowired
    private ForeignerProfileSearchService foreignerProfileSearchService;

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

    @Autowired
    private ForeignerProfileTestFixture foreignerFixture;

    @Autowired
    private AgentProfileTestFixture agentFixture;

    @Autowired
    private UserTestFixture userFixture;

    @Test
    @DisplayName("행정사 전문분야와 겹치는 외국인을 추천한다 (Manual Setup)")
    void findForeignerCardMatchOnSpecializedJob_integration() {
        // given
        String email = "agent@test.com";
        User agentUser = userRepository.save(new User(email, "password", UserType.VALID_AGENT,
                LoginType.EMAIL, true));

        AgentProfile agentProfile = new AgentProfile(
                "Agent Name", LocalDate.now(), "url", "09:00", "Office", "Addr", "Detail", "Hist",
                "010-1234-1234", agentUser.getId(), "Lic", LocalDate.now(), "Page", "Mgmt", "Comment");
        agentProfileRepository.save(agentProfile);

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "JC001", "Visa", new float[512], null));

        AgentSpecializedJob specializedJob = new AgentSpecializedJob(agentProfile, jobCode);
        agentSpecializedJobRepository.save(specializedJob);

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
        List<ForeignerCardResponse> result = foreignerProfileSearchService
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
        String email = "agent_empty@test.com";
        User agentUser = userRepository.save(new User(email, "password", UserType.VALID_AGENT,
                LoginType.EMAIL, true));

        AgentProfile agentProfile = new AgentProfile(
                "Agent Empty", LocalDate.now(), "url", "09:00", "Office", "Addr", "Detail", "Hist",
                "010-1234-1234", agentUser.getId(), "Lic", LocalDate.now(), "Page", "Mgmt", "Comment");
        agentProfileRepository.save(agentProfile);

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "JC002", "Visa2", new float[512], null));

        AgentSpecializedJob specializedJob = new AgentSpecializedJob(agentProfile, jobCode);
        agentSpecializedJobRepository.save(specializedJob);

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
        List<ForeignerCardResponse> result = foreignerProfileSearchService.findForeignerCardMatchOnSpecializedJob(email);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("행정사의 특화 직무와 매칭되는 외국인 프로필을 조회한다 (Fixture Usage)")
    void findForeignerCardMatchOnSpecializedJob_ShouldReturnMatchedProfiles() {
        // given
        // 1. 행정사 생성 (특화 직무: Backend)
        JobGroup devGroup = agentFixture.createJobGroup("IT 개발");
        JobCode backendJob = agentFixture.createJobCode("DEV-001", "BackEnd", devGroup);
        JobCode frontendJob = agentFixture.createJobCode("DEV-002", "FrontEnd", devGroup);

        String agentEmail = "agent_fixture@test.com";
        User agentUser = userFixture.createUser(agentEmail, UserType.VALID_AGENT);
        AgentProfile agentProfile = agentFixture.createAgentProfile("Agent Kim", "Seoul", agentUser.getId());
        agentFixture.createAgentSpecializedJob(agentProfile, backendJob);

        // 2. 외국인 생성
        // Case 1: 매칭되는 외국인 (Backend Similarity)
        createForeignerWithSimilarity("Matched Foreigner", backendJob.getId(), "Backend Developer");

        // Case 2: 매칭되지 않는 외국인 (Frontend Similarity only)
        createForeignerWithSimilarity("Unmatched Foreigner", frontendJob.getId(), "Frontend Developer");

        // Case 3: 매칭되지 않는 외국인 (No Similarity)
        createForeignerWithSimilarity("No Match Foreigner", null, "Chef");

        em.flush();
        em.clear();

        // when
        List<ForeignerCardResponse> responses = foreignerProfileSearchService
                .findForeignerCardMatchOnSpecializedJob(agentEmail);

        // then
        assertThat(responses).hasSize(1);
        ForeignerCardResponse response = responses.get(0);
        assertThat(response.nickname()).startsWith("Matched Foreigner");
        assertThat(response.jobTitle()).isEqualTo("Backend Developer");
    }

    @Test
    @DisplayName("복합 필터링(직업군+국적+언어) 조건에 맞는 외국인 프로필 카드를 조회한다")
    void findForeignerProfileCardsBasedOnFilter_ShouldReturnMatchedProfiles() {
        // given
        // 1. 기초 데이터 (직업군, 국적, 언어)
        JobGroup devGroup = agentFixture.createJobGroup("IT 개발");
        JobCode backendJob = agentFixture.createJobCode("DEV-001", "BackEnd", devGroup);
        JobCode frontendJob = agentFixture.createJobCode("DEV-002", "FrontEnd", devGroup);

        Nationality usa = agentFixture.createNationality("USA");
        Nationality korea = agentFixture.createNationality("Korea");

        Language english = agentFixture.createLanguage("English");
        Language korean = agentFixture.createLanguage("Korean");

        // 2. 외국인 데이터 생성
        // Target: IT 직무 희망 + USA 국적 + English 가능
        createForeigner("Target Foreigner", backendJob.getId(), usa, english, "Backend Developer");

        // Non-Target 1: 직무 불일치 (No Similarity)
        createForeigner("No Job Match", null, usa, english, "Chef");

        // Non-Target 2: 국적 불일치 (Korea)
        createForeigner("Wrong Nation", backendJob.getId(), korea, english, "Backend Developer");

        // Non-Target 3: 언어 불일치 (Korean only)
        createForeigner("Wrong Lang", backendJob.getId(), usa, korean, "Backend Developer");

        em.flush();
        em.clear();

        // when
        ForeignerCardRequest request = new ForeignerCardRequest(
                List.of("IT 개발"), // JobGroup -> JobCode Ids 변환 테스트 포함
                List.of(usa.getId()),
                List.of(english.getId()));

        SliceResponse<ForeignerCardExtensionResponse, UUID> response = foreignerProfileSearchService
                .findForeignerProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10));

        // then
        assertThat(response.content()).hasSize(1);
        ForeignerCardExtensionResponse card = response.content().get(0);
        assertThat(card.nickname()).startsWith("Target Foreigner");
        assertThat(card.jobTitle()).isEqualTo("Backend Developer");
        assertThat(card.nationIdList()).contains(usa.getId());
        assertThat(card.languageIdList()).contains(english.getId());
    }

    @Test
    @DisplayName("필터 조건이 없을 경우 전체 목록을 조회한다")
    void findForeignerProfileCardsBasedOnFilter_ShouldReturnAllIdleProfiles_WhenNoFilter() {
        // given
        User user1 = userFixture.createUser("f1@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile p1 = foreignerFixture.createForeignerProfile(user1);
        foreignerFixture.createForeignerSimilarity(p1, new long[]{100L});
        foreignerFixture.createForeignerEducation(p1);
        foreignerFixture.createForeignerExpectedCompany(p1, "Job1");

        User user2 = userFixture.createUser("f2@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile p2 = foreignerFixture.createForeignerProfile(user2);
        foreignerFixture.createForeignerSimilarity(p2, new long[]{200L});
        foreignerFixture.createForeignerEducation(p2);
        foreignerFixture.createForeignerExpectedCompany(p2, "Job2");

        em.flush();
        em.clear();

        // when
        ForeignerCardRequest request = new ForeignerCardRequest(null, null, null);
        SliceResponse<ForeignerCardExtensionResponse, UUID> response = foreignerProfileSearchService
                .findForeignerProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10));

        // then
        assertThat(response.content()).hasSize(2);
    }

    private void createForeigner(String nicknameAlias, Long jobCodeId, Nationality nationality, Language language,
                                 String jobTitle) {
        User user = userFixture.createUser(UUID.randomUUID() + "@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile profile = foreignerFixture.createForeignerProfile(user, nicknameAlias);

        long[] jobIds = (jobCodeId != null) ? new long[]{jobCodeId} : new long[]{};
        foreignerFixture.createForeignerSimilarity(profile, jobIds);

        foreignerFixture.createForeignerEducation(profile);
        foreignerFixture.createForeignerExpectedCompany(profile, jobTitle);

        if (nationality != null) {
            foreignerFixture.createForeignerNationality(profile, nationality);
        }

        if (language != null) {
            foreignerFixture.createForeignerLanguage(profile, language);
        }
    }

    private void createForeignerWithSimilarity(String nicknameAlias, Long jobCodeId, String jobTitle) {
        User user = userFixture.createUser(UUID.randomUUID() + "@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile profile = foreignerFixture.createForeignerProfile(user, nicknameAlias);

        long[] jobIds = (jobCodeId != null) ? new long[] { jobCodeId } : new long[] {};
        foreignerFixture.createForeignerSimilarity(profile, jobIds);

        foreignerFixture.createForeignerExpectedCompany(profile, jobTitle);
    }
}
