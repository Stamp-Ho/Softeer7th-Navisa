package com.navisa.be.foreigner.service;

import com.navisa.be.common.infrastructure.client.GeminiTextEmbeddingClient;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.common.repository.NationalityRepository;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.ForeignerFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
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

        @MockitoBean
        private JobCodeRepository jobCodeRepository;

        @Test
        @DisplayName("Facade를 통해 외국인 정보 등록과 유사도 계산 프로세스가 정상 수행된다")
        void registerAllForeignerInfo() {
                // given
                Language language = languageRepository.save(new Language(null, "English"));
                Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));

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

                given(userQueryService.findByEmail(email))
                                .willReturn(mockUser);

                given(jobCodeRepository.findTop3SimilarJobCodes(any()))
                                .willReturn(List.of(
                                                new JobCodeSimilarityProjectionImpl(1L, 0.9),
                                                new JobCodeSimilarityProjectionImpl(2L, 0.8),
                                                new JobCodeSimilarityProjectionImpl(3L, 0.7)));

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

                given(jobCodeRepository.findTop3SimilarJobCodes(any()))
                                .willReturn(List.of(
                                                new JobCodeSimilarityProjectionImpl(1L, 0.9),
                                                new JobCodeSimilarityProjectionImpl(2L, 0.8),
                                                new JobCodeSimilarityProjectionImpl(3L, 0.7)));

                // 먼저 데이터 저장을 위해 register 호출
                foreignerServiceFacade.registerAllForeignerInfo(request, email);

                em.flush();
                em.clear();

                // when
                ForeignerQueryResponse response = foreignerServiceFacade.findForeignerTotalInfo(email);

                // then
                assertThat(response).isNotNull();
                assertThat(response.isIdle()).isEqualTo(request.isIdle());
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

                // Mocking jobCodeRepository to return 2 items instead of 3
                given(jobCodeRepository.findTop3SimilarJobCodes(any()))
                                .willReturn(List.of(
                                                new JobCodeSimilarityProjectionImpl(1L, 0.9),
                                                new JobCodeSimilarityProjectionImpl(2L, 0.8)));

                // when & then
                assertThatThrownBy(() -> foreignerServiceFacade.registerAllForeignerInfo(request, email))
                                .isInstanceOf(BaseException.class)
                                .hasFieldOrPropertyWithValue("status",
                                                ResponseStatus.SIMILARITY_CALCULATE_FAIL);
        }

        // Projection 구현체 (테스트용)
        static class JobCodeSimilarityProjectionImpl
                        implements com.navisa.be.common.dto.projection.JobCodeSimilarityProjection {
                private final Long id;
                private final Double similarity;

                public JobCodeSimilarityProjectionImpl(Long id, Double similarity) {
                        this.id = id;
                        this.similarity = similarity;
                }

                @Override
                public Long getId() {
                        return id;
                }

                @Override
                public String getName() {
                        return "Dummy Job";
                }

                @Override
                public Double getSimilarity() {
                        return similarity;
                }
        }
}
