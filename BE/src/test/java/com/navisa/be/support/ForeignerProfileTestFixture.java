package com.navisa.be.support;

import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.foreigner.model.entity.*;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.*;
import com.navisa.be.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class ForeignerProfileTestFixture {

    private final ForeignerProfileRepository foreignerProfileRepository;
    private final ForeignerSimilarityRepository foreignerSimilarityRepository;
    private final ForeignerNationalityRepository foreignerNationalityRepository;
    private final ForeignerLanguageRepository foreignerLanguageRepository;
    private final ForeignerEducationRepository foreignerEducationRepository;
    private final ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;

    public ForeignerProfile createForeignerProfile(User loginUser) {
        ForeignerProfile foreignerProfile = new ForeignerProfile(loginUser.getId(), ForeignerSearchStatus.IDLE);
        return foreignerProfileRepository.save(foreignerProfile);
    }

    public ForeignerProfile createForeignerProfile(User loginUser, String nickname) {
        ForeignerProfile foreignerProfile = new ForeignerProfile(loginUser.getId(), nickname,
                ForeignerSearchStatus.IDLE);
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
