package com.navisa.be.foreigner.service;

import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.global.common.repository.LanguageRepository;
import com.navisa.be.global.common.repository.NationalityRepository;
import com.navisa.be.global.infra.embedding.GeminiTextEmbeddingClient;
import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Transactional
public class ForeignerRegistrationServiceTest extends IntegrationTestSupport {

    @Autowired
    private ForeignerRegistrationService foreignerRegistrationService;

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private NationalityRepository nationalityRepository;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private EntityManager em;

    @MockitoBean
    private GeminiTextEmbeddingClient geminiTextEmbeddingClient;

    @MockitoBean
    private UserCrudService userCrudService;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Test
    @DisplayName("외국인 정보 등록과 유사도 계산 프로세스가 정상 수행된다")
    void registerAllForeignerInfo() {
        // given
        Language language = languageRepository.save(new Language(null, "English"));
        Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));

        // JobCode 데이터 준비 (3개 이상)
        jobCodeRepository.save(new JobCode(null, "J001", "Job 1",
                new float[512], null));
        jobCodeRepository.save(new JobCode(null, "J002", "Job 2",
                new float[512], null));
        jobCodeRepository.save(new JobCode(null, "J003", "Job 3",
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

        given(userCrudService.findByEmail(email)).willReturn(mockUser);
        given(userCrudService.findById(userId)).willReturn(mockUser);

        // when
        foreignerRegistrationService.registerAllForeignerInfo(request, email);

        // then
        ForeignerProfile foundProfile = foreignerProfileRepository.findAll().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElse(null);

        assertThat(foundProfile).isNotNull();

        // 신청서가 존재하는지만 확인하면 충분
        boolean formExists = applicationFormRepository.findAll().stream()
                .anyMatch(form -> form.getForeignerProfile().getId().equals(foundProfile.getId()));
        assertThat(formExists).isTrue();
    }

    @Test
    @DisplayName("유사도 계산 결과가 3개가 아니면 예외가 발생한다")
    void registerAllForeignerInfo_Fail_When_Similarity_Calculation_Returns_Not_3_Items() {
        // given
        Language language = languageRepository.save(new Language(null, "English"));
        Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));

        // JobCode 데이터 준비 (2개만 저장 -> 3개 미만이므로 예외 발생 예상)
        jobCodeRepository.save(new JobCode(null, "J001", "Job 1", new float[512], null));
        jobCodeRepository.save(new JobCode(null, "J002", "Job 2", new float[512], null));

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

        given(userCrudService.findByEmail(email))
                .willReturn(mockUser);
        given(userCrudService.findById(userId))
                .willReturn(mockUser);

        // when & then
        assertThatThrownBy(() -> foreignerRegistrationService.registerAllForeignerInfo(request, email))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("status",
                        ResponseStatus.SIMILARITY_CALCULATE_FAIL);
    }
}
