package com.navisa.be.support;

import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.foreigner.model.entity.*;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.global.common.repository.LanguageRepository;
import com.navisa.be.global.common.repository.NationalityRepository;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.*;
import com.navisa.be.user.model.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import java.util.List;

@RequiredArgsConstructor
@Component
@Transactional
public class ForeignerProfileTestFixture {

    private final ForeignerProfileRepository foreignerProfileRepository;
    private final ForeignerSimilarityRepository foreignerSimilarityRepository;
    private final ForeignerNationalityRepository foreignerNationalityRepository;
    private final ForeignerLanguageRepository foreignerLanguageRepository;
    private final ForeignerEducationRepository foreignerEducationRepository;
    private final ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;
    private final ForeignerCareersRepository foreignerCareersRepository;
    private final LanguageRepository languageRepository;
    private final NationalityRepository nationalityRepository;
    private final EntityManager em;

    public ForeignerProfile createForeignerProfile(User foreignerUser) {
        ForeignerProfile foreignerProfile = new ForeignerProfile(foreignerUser.getId(), ForeignerSearchStatus.REQUESTING);

        ForeignerProfile savedForeigner = foreignerProfileRepository.save(foreignerProfile);

        ForeignerEducation education = new ForeignerEducation(null, savedForeigner.getId(), EducationDegreeLevel.ABOVE_MASTER, "한국대학교", "대박전공");
        foreignerEducationRepository.save(education);

        List<LocalDate[]> datePairs = List.of(
                new LocalDate[]{LocalDate.of(2025, 1, 1), LocalDate.of(2025, 9, 1)},
                new LocalDate[]{LocalDate.of(2026, 5, 1), LocalDate.of(2026, 7, 1)}
        );

        for (int i = 0; i < 2; i++) {
            ForeignerCareers career = new ForeignerCareers(null, foreignerProfile.getId(), "옛 회사" + i, "옛 직무" + i, datePairs.get(i)[0], datePairs.get(i)[1], false);
            foreignerCareersRepository.save(career);
        }

        ForeignerSimilarity similarity = new ForeignerSimilarity(null, foreignerProfile.getId(), new double[3], new long[3]);
        foreignerSimilarityRepository.save(similarity);

        Language lang = new Language(null, "한국어");
        languageRepository.save(lang);

        ForeignerLanguage foreignerLanguage = new ForeignerLanguage(null, foreignerProfile, lang);
        foreignerLanguageRepository.save(foreignerLanguage);

        ForeignerExpectedCompany expectedCompany = new ForeignerExpectedCompany(null, foreignerProfile.getId(), "새 회사", "새 직무", LocalDate.of(2026, 1, 1));
        foreignerExpectedCompanyRepository.save(expectedCompany);

        Nationality nation = new Nationality(null, "외국");
        nationalityRepository.save(nation);
        ForeignerNationality foreignerNationality = new ForeignerNationality(null, foreignerProfile, nation);
        foreignerNationalityRepository.save(foreignerNationality);

        em.flush();
        em.clear();

        return savedForeigner;
    }

    public ForeignerProfile createForeignerProfile(User loginUser, String nickname) {
        ForeignerProfile foreignerProfile = new ForeignerProfile(loginUser.getId(), nickname,
                ForeignerSearchStatus.REQUESTING);
        return foreignerProfileRepository.save(foreignerProfile);
    }

    public ForeignerSimilarity createForeignerSimilarity(ForeignerProfile profile, long[] jobIds) {
        ForeignerSimilarity similarity = new ForeignerSimilarity(null, profile.getId(), new double[] {}, jobIds);
        return foreignerSimilarityRepository.save(similarity);
    }

    public void createForeignerNationality(ForeignerProfile profile, Nationality nationality) {
        ForeignerNationality entity = new ForeignerNationality(null, profile, nationality);
        foreignerNationalityRepository.save(entity);
    }

    public void createForeignerLanguage(ForeignerProfile profile, Language language) {
        ForeignerLanguage entity = new ForeignerLanguage(null, profile, language);
        foreignerLanguageRepository.save(entity);
    }

    public void createForeignerEducation(ForeignerProfile profile) {
        ForeignerEducation entity = new ForeignerEducation(null, profile.getId(), EducationDegreeLevel.BACHELOR, "Univ",
                "CS");
        foreignerEducationRepository.save(entity);
    }

    public void createForeignerExpectedCompany(ForeignerProfile profile, String jobTitle) {
        ForeignerExpectedCompany entity = new ForeignerExpectedCompany(null, profile.getId(), "IT", jobTitle,
                LocalDate.now());
        foreignerExpectedCompanyRepository.save(entity);
    }
}
