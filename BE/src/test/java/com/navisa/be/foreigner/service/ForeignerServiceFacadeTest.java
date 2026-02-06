package com.navisa.be.foreigner.service;

import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.common.infrastructure.client.GeminiTextEmbeddingClient;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.common.repository.NationalityRepository;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.foreigner.dto.request.ForeignerCardRequest;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerCardExtensionResponse;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.info.model.entity.JobGroup;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.ForeignerFixture;
import com.navisa.be.support.ForeignerProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Transactional
class ForeignerServiceFacadeTest extends IntegrationTestSupport {

        @Autowired
        private ForeignerServiceFacade foreignerServiceFacade;

        @Autowired
        private ForeignerProfileRepository foreignerProfileRepository;

        @Autowired
        private LanguageRepository languageRepository;

        @Autowired
        private NationalityRepository nationalityRepository;

        @Autowired
        private EntityManager em;

        @MockitoBean
        private GeminiTextEmbeddingClient geminiTextEmbeddingClient;

        @MockitoBean
        private UserQueryService userQueryService;

        @Autowired
        private JobCodeRepository jobCodeRepository;

        @Autowired
        private ForeignerProfileTestFixture foreignerFixture;

        @Autowired
        private AgentProfileTestFixture agentFixture;

        @Autowired
        private UserTestFixture userFixture;

        @Test
        @DisplayName("Facade를 통해 외국인 정보 등록과 유사도 계산 프로세스가 정상 수행된다")
        void registerAllForeignerInfo() {
                // given
                Language language = languageRepository.save(new Language(null, "English"));
                Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));

                // JobCode 데이터 준비 (3개 이상)
                jobCodeRepository.save(new com.navisa.be.common.model.entity.JobCode(null, "J001", "Job 1",
                                new float[512], null));
                jobCodeRepository.save(new com.navisa.be.common.model.entity.JobCode(null, "J002", "Job 2",
                                new float[512], null));
                jobCodeRepository.save(new com.navisa.be.common.model.entity.JobCode(null, "J003", "Job 3",
                                new float[512], null));

                ForeignerRegisterRequest request = ForeignerFixture.createForeignerRegisterRequest(
                                List.of(nationality.getId()),
                                List.of(language.getId()),
                                true);
                UUID userId = UUID.randomUUID();
                String email = "test@example.com";
                User mockUser = User.createGoogleUser(email, UserType.UNFILLED_FOREIGNER);
                ReflectionTestUtils.setField(mockUser, "id", userId);

                float[] mockEmbedding = new float[512]; // 512차원 더미 벡터

                given(geminiTextEmbeddingClient.embedText(any(), any()))
                                .willReturn(mockEmbedding);

                given(userQueryService.findByEmail(email)).willReturn(mockUser);
                given(userQueryService.findById(userId)).willReturn(mockUser);

                // when
                foreignerServiceFacade.registerAllForeignerInfo(request, email);

                // then
                ForeignerProfile foundProfile = foreignerProfileRepository.findAll().stream()
                                .filter(p -> p.getUserId().equals(userId))
                                .findFirst()
                                .orElse(null);

                assertThat(foundProfile).isNotNull();
        }

        @Test
        @DisplayName("Facade를 통해 외국인 전체 정보를 조회한다")
        void findForeignerTotalInfo() {
                // given
                Language language = languageRepository.save(new Language(null, "English"));
                Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));

                // JobCode 데이터 준비
                jobCodeRepository.save(new com.navisa.be.common.model.entity.JobCode(null, "J001", "Job 1",
                                new float[512], null));
                jobCodeRepository.save(new com.navisa.be.common.model.entity.JobCode(null, "J002", "Job 2",
                                new float[512], null));
                jobCodeRepository.save(new com.navisa.be.common.model.entity.JobCode(null, "J003", "Job 3",
                                new float[512], null));

                ForeignerRegisterRequest request = ForeignerFixture.createForeignerRegisterRequest(
                                List.of(nationality.getId()),
                                List.of(language.getId()),
                                true);
                UUID userId = UUID.randomUUID();
                String email = "test@example.com";
                User mockUser = User.createGoogleUser(email, UserType.UNFILLED_FOREIGNER);
                ReflectionTestUtils.setField(mockUser, "id", userId);

                float[] mockEmbedding = new float[512];

                given(geminiTextEmbeddingClient.embedText(any(), any())).willReturn(mockEmbedding);

                given(userQueryService.findByEmail(email)).willReturn(mockUser);
                given(userQueryService.findById(userId)).willReturn(mockUser);

                // 먼저 데이터 저장을 위해 register 호출
                foreignerServiceFacade.registerAllForeignerInfo(request, email);

                em.flush();
                em.clear();

                // when
                ForeignerQueryResponse response = foreignerServiceFacade.findForeignerTotalInfo(email);

                // then
                assertThat(response).isNotNull();
                assertThat(response.isRequesting()).isEqualTo(request.isRequesting());
                assertThat(response.education().schoolName()).isEqualTo(request.education().schoolName());
                assertThat(response.languageIdList()).contains(language.getId());
                assertThat(response.nationIdList()).contains(nationality.getId());
        }

        @Test
        @DisplayName("유사도 계산 결과가 3개가 아니면 예외가 발생한다")
        void registerAllForeignerInfo_Fail_When_Similarity_Calculation_Returns_Not_3_Items() {
                // given
                Language language = languageRepository.save(new Language(null, "English"));
                Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));

                // JobCode 데이터 준비 (2개만 저장 -> 3개 미만이므로 예외 발생 예상)
                jobCodeRepository.save(new com.navisa.be.common.model.entity.JobCode(null, "J001", "Job 1",
                                new float[512], null));
                jobCodeRepository.save(new com.navisa.be.common.model.entity.JobCode(null, "J002", "Job 2",
                                new float[512], null));

                ForeignerRegisterRequest request = ForeignerFixture.createForeignerRegisterRequest(
                                List.of(nationality.getId()),
                                List.of(language.getId()),
                                true);
                UUID userId = UUID.randomUUID();
                String email = "test@example.com";
                User mockUser = User.createGoogleUser(email, UserType.UNFILLED_FOREIGNER);
                ReflectionTestUtils.setField(mockUser, "id", userId);

                float[] mockEmbedding = new float[512];

                given(geminiTextEmbeddingClient.embedText(any(), any()))
                                .willReturn(mockEmbedding);

                given(userQueryService.findByEmail(email))
                                .willReturn(mockUser);
                given(userQueryService.findById(userId))
                                .willReturn(mockUser);

                // when & then
                assertThatThrownBy(() -> foreignerServiceFacade.registerAllForeignerInfo(request, email))
                                .isInstanceOf(BaseException.class)
                                .hasFieldOrPropertyWithValue("status",
                                                ResponseStatus.SIMILARITY_CALCULATE_FAIL);
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

                SliceResponse<ForeignerCardExtensionResponse, UUID> response = foreignerServiceFacade
                                .findForeignerProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10));

                // then
                assertThat(response.content()).hasSize(1);
                ForeignerCardExtensionResponse card = response.content().get(0);
                assertThat(card.getNickname()).startsWith("Target Foreigner");
                assertThat(card.getJobTitle()).isEqualTo("Backend Developer");
                assertThat(card.getNationIdList()).contains(usa.getId());
                assertThat(card.getLanguageIdList()).contains(english.getId());
        }

        @Test
        @DisplayName("필터 조건이 없을 경우 전체 목록을 조회한다 (단, REQUESTING 상태만)")
        void findForeignerProfileCardsBasedOnFilter_ShouldReturnAllIdleProfiles_WhenNoFilter() {
                // given
                User user1 = userFixture.createUser("f1@test.com", UserType.FILLED_FOREIGNER);
                ForeignerProfile p1 = foreignerFixture.createForeignerProfile(user1);
                foreignerFixture.createForeignerSimilarity(p1, new long[] { 100L });
                foreignerFixture.createForeignerEducation(p1);
                foreignerFixture.createForeignerExpectedCompany(p1, "Job1");

                User user2 = userFixture.createUser("f2@test.com", UserType.FILLED_FOREIGNER);
                ForeignerProfile p2 = foreignerFixture.createForeignerProfile(user2);
                foreignerFixture.createForeignerSimilarity(p2, new long[] { 200L });
                foreignerFixture.createForeignerEducation(p2);
                foreignerFixture.createForeignerExpectedCompany(p2, "Job2");

                em.flush();
                em.clear();

                // when
                ForeignerCardRequest request = new ForeignerCardRequest(null, null, null);
                SliceResponse<ForeignerCardExtensionResponse, UUID> response = foreignerServiceFacade
                                .findForeignerProfileCardsBasedOnFilter(request, new SliceRequest<>(null, 10));

                // then
                assertThat(response.content()).hasSize(2);
        }

        private void createForeigner(String nicknameAlias, Long jobCodeId, Nationality nationality, Language language,
                        String jobTitle) {
                User user = userFixture.createUser(UUID.randomUUID() + "@test.com", UserType.FILLED_FOREIGNER);
                ForeignerProfile profile = foreignerFixture.createForeignerProfile(user, nicknameAlias);

                // Similarity (JobCode)
                long[] jobIds = (jobCodeId != null) ? new long[] { jobCodeId } : new long[] {};
                foreignerFixture.createForeignerSimilarity(profile, jobIds);

                // Necessary Info for Response (Education, ExpectedCompany)
                foreignerFixture.createForeignerEducation(profile);
                foreignerFixture.createForeignerExpectedCompany(profile, jobTitle);

                // Nationality (Optional but used for filter)
                if (nationality != null) {
                        foreignerFixture.createForeignerNationality(profile, nationality);
                }

                // Language (Optional but used for filter)
                if (language != null) {
                        foreignerFixture.createForeignerLanguage(profile, language);
                }
        }
}
