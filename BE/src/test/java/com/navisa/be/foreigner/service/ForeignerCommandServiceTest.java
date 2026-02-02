package com.navisa.be.foreigner.service;

import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.common.repository.NationalityRepository;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.*;
import com.navisa.be.support.ForeignerFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import com.navisa.be.foreigner.repository.ForeignerEducationRepository;
import com.navisa.be.foreigner.repository.ForeignerCareersRepository;
import com.navisa.be.foreigner.repository.ForeignerExpectedCompanyRepository;
import com.navisa.be.foreigner.repository.ForeignerLanguageRepository;
import com.navisa.be.foreigner.repository.ForeignerNationalityRepository;
import com.navisa.be.foreigner.repository.ForeignerSimilarityRepository;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.common.dto.projection.JobCodeSimilarityProjection;

@SpringBootTest
@Transactional
class ForeignerCommandServiceTest extends IntegrationTestSupport {

    @Autowired
    private ForeignerCommandService foreignerCommandService;

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private NationalityRepository nationalityRepository;

    @Autowired
    private ForeignerEducationRepository foreignerEducationRepository;

    @Autowired
    private ForeignerCareersRepository foreignerCareersRepository;

    @Autowired
    private ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;

    @Autowired
    private ForeignerLanguageRepository foreignerLanguageRepository;

    @Autowired
    private ForeignerNationalityRepository foreignerNationalityRepository;

    @Autowired
    private com.navisa.be.user.repository.UserRepository userRepository;

    @Autowired
    private ForeignerSimilarityRepository foreignerSimilarityRepository;

    @Test
    @DisplayName("ForeignerRegisterRequest로 외국인 정보를 등록하면 관련된 모든 엔티티가 저장된다")
    void registerForeignerTotalInfo() {
        // given
        Language language = languageRepository.save(new Language(null, "English"));
        Nationality nationality = nationalityRepository.save(new Nationality(null, "USA"));

        ForeignerRegisterRequest request = ForeignerFixture.createForeignerRegisterRequest(
                List.of(nationality.getId()),
                List.of(language.getId()),
                false);

        User savedUser = userRepository.save(User.createGoogleUser("test@example.com", UserType.UNFILLED_FOREIGNER));
        UUID userId = savedUser.getId();

        // when
        ForeignerProfile savedProfile = foreignerCommandService.registerForeignerTotalInfo(request, userId);

        // then
        assertThat(savedProfile).isNotNull();
        assertThat(savedProfile.getId()).isNotNull();
        assertThat(savedProfile.getUserId()).isEqualTo(userId);

        ForeignerProfile foundProfile = foreignerProfileRepository.findById(savedProfile.getId()).orElseThrow();
        assertThat(foundProfile.getUserId()).isEqualTo(userId);

        // Verify connected entities
        var savedEducation = foreignerEducationRepository.findByForeignerId(foundProfile.getId()).orElseThrow();
        assertThat(savedEducation.getSchoolName()).isEqualTo(request.education().schoolName());

        var savedCareers = foreignerCareersRepository.findByForeignerId(foundProfile.getId());
        assertThat(savedCareers).hasSize(1);
        assertThat(savedCareers.get(0).getCompanyName()).isEqualTo(request.foreignerCareers().get(0).companyName());

        var savedCompany = foreignerExpectedCompanyRepository.findByForeignerId(foundProfile.getId()).orElseThrow();
        assertThat(savedCompany.getCompanyName()).isEqualTo(request.expectedCompany().companyName());

        var savedLanguages = foreignerLanguageRepository.findByForeignerProfileId(foundProfile.getId());
        assertThat(savedLanguages).hasSize(1);
        assertThat(savedLanguages.get(0).getLanguage().getId()).isEqualTo(language.getId());

        var savedNationalities = foreignerNationalityRepository.findByForeignerProfileId(foundProfile.getId());
        assertThat(savedNationalities).hasSize(1);
        assertThat(savedNationalities.get(0).getNationality().getId()).isEqualTo(nationality.getId());
    }

    @Test
    @DisplayName("이미 존재하는 userId로 등록 요청 시 변경된 정보를 업데이트한다 (Upsert)")
    void registerForeignerTotalInfo_Upsert() {
        // given
        Language langEng = languageRepository.save(new Language(null, "English"));
        Nationality natUSA = nationalityRepository.save(new Nationality(null, "USA"));

        ForeignerRegisterRequest initialRequest = ForeignerFixture.createForeignerRegisterRequest(
                List.of(natUSA.getId()),
                List.of(langEng.getId()),
                true);
        User savedUser = userRepository.save(User.createGoogleUser("upsert@example.com", UserType.UNFILLED_FOREIGNER));
        UUID userId = savedUser.getId();

        ForeignerProfile initialProfile = foreignerCommandService.registerForeignerTotalInfo(initialRequest, userId);

        var preUpdateEducation = foreignerEducationRepository.findByForeignerId(initialProfile.getId()).orElseThrow();
        Long preUpdateEducationId = preUpdateEducation.getId();

        Language langKor = languageRepository.save(new Language(null, "Korean"));
        Nationality natCan = nationalityRepository.save(new Nationality(null, "Canada"));

        ForeignerRegisterRequest updateRequest = ForeignerFixture.createForeignerRegisterRequest(
                List.of(natCan.getId()),
                List.of(langKor.getId()),
                true);

        // when
        ForeignerProfile updatedProfile = foreignerCommandService.registerForeignerTotalInfo(updateRequest, userId);

        // then
        assertThat(updatedProfile.getId()).isEqualTo(initialProfile.getId());
        assertThat(updatedProfile.getUserId()).isEqualTo(userId);

        var savedLanguages = foreignerLanguageRepository.findByForeignerProfileId(updatedProfile.getId());
        assertThat(savedLanguages).hasSize(1);
        assertThat(savedLanguages.get(0).getLanguage().getId()).isEqualTo(langKor.getId());
        assertThat(savedLanguages.get(0).getLanguage().getId()).isNotEqualTo(langEng.getId());

        var savedNationalities = foreignerNationalityRepository.findByForeignerProfileId(updatedProfile.getId());
        assertThat(savedNationalities).hasSize(1);
        assertThat(savedNationalities.get(0).getNationality().getId()).isEqualTo(natCan.getId());

        var savedEducation = foreignerEducationRepository.findByForeignerId(updatedProfile.getId()).orElseThrow();
        assertThat(savedEducation.getId()).isEqualTo(preUpdateEducationId);
        assertThat(savedEducation.getSchoolName()).isEqualTo(updateRequest.education().schoolName());
    }

    @Test
    @DisplayName("Calculated Similarity가 정상적으로 저장된다")
    void registerCalculatedSimilarity() {
        // given
        ForeignerProfile profile = foreignerProfileRepository
                .save(new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.REQUESTING));

        List<JobCodeSimilarityProjection> projections = List.of(
                new JobCodeSimilarityProjection() {
                    @Override
                    public Long getId() {
                        return 100L;
                    }

                    @Override
                    public String getName() {
                        return "Job A";
                    }

                    @Override
                    public Double getSimilarity() {
                        return 0.85;
                    }
                },
                new JobCodeSimilarityProjection() {
                    @Override
                    public Long getId() {
                        return 200L;
                    }

                    @Override
                    public String getName() {
                        return "Job B";
                    }

                    @Override
                    public Double getSimilarity() {
                        return 0.75;
                    }
                });

        // when
        foreignerCommandService.registerCalculatedSimilarity(profile, projections);

        // then
        var savedSimilarity = foreignerSimilarityRepository.findByForeignerId(profile.getId()).orElseThrow();
        assertThat(savedSimilarity.getJobCodeIdList()).containsExactly(100L, 200L);
        assertThat(savedSimilarity.getSimilarityList()).containsExactly(0.85, 0.75);
    }
}
