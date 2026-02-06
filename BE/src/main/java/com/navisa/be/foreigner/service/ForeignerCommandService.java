package com.navisa.be.foreigner.service;

import com.navisa.be.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.common.repository.NationalityRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import org.springframework.stereotype.Service;

import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ForeignerCommandService {

        private final ForeignerProfileRepository foreignerProfileRepository;
        private final LanguageRepository languageRepository;
        private final NationalityRepository nationalityRepository;
        private final ForeignerRelationCommandService foreignerRelationCommandService;
        private final UserQueryService userQueryService;

        @Transactional
        public ForeignerProfile registerForeignerTotalInfo(ForeignerRegisterRequest request, UUID userId) {
                // 검증 로직
                List<Language> languages = languageRepository.findAllById(request.languageIdList());
                if (languages.size() != request.languageIdList().size()) {
                        throw new BaseException(ResponseStatus.INVALID_LANGUAGE);
                }
                List<Nationality> nationalities = nationalityRepository.findAllById(request.nationIdList());
                if (nationalities.size() != request.nationIdList().size()) {
                        throw new BaseException(ResponseStatus.INVALID_NATIONALITY);
                }

                User findUser = userQueryService.findById(userId);
                if (findUser.getUserType().equals(UserType.UNFILLED_FOREIGNER)) {
                        findUser.upgradeToValidForeigner();
                }

                return foreignerProfileRepository.findByUserId(userId)
                                .map(existingProfile -> updateForeignerProfile(existingProfile, request, languages,
                                                nationalities))
                                .orElseGet(() -> createForeignerProfile(request, userId, languages, nationalities));
        }

        private ForeignerProfile createForeignerProfile(ForeignerRegisterRequest request, UUID userId,
                        List<Language> languages, List<Nationality> nationalities) {
                ForeignerProfile profile = request.toProfileEntity(userId);
                ForeignerProfile savedProfile = foreignerProfileRepository.save(profile);

                foreignerRelationCommandService.saveForeignerRelations(savedProfile, request, languages, nationalities);

                return savedProfile;
        }

        private ForeignerProfile updateForeignerProfile(ForeignerProfile profile, ForeignerRegisterRequest request,
                        List<Language> languages, List<Nationality> nationalities) {

                profile.updateStatus(request.isRequesting() ? ForeignerSearchStatus.REQUESTING : ForeignerSearchStatus.IDLE);

                foreignerRelationCommandService.updateLanguages(profile, languages);
                foreignerRelationCommandService.updateNationalities(profile, nationalities);
                foreignerRelationCommandService.updateEducation(profile, request.education());
                foreignerRelationCommandService.updateCareers(profile, request.foreignerCareers());
                foreignerRelationCommandService.updateExpectedCompany(profile, request.expectedCompany());

                return profile;
        }

        @Transactional
        public void registerCalculatedSimilarity(ForeignerProfile profile,
                        List<JobCodeSimilarityProjection> projectionList) {
                foreignerRelationCommandService.registerCalculatedSimilarity(profile, projectionList);
        }
}
