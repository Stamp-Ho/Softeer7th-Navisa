package com.navisa.be.foreigner.service;

import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.common.service.JobCodeService;
import com.navisa.be.global.common.service.LanguageService;
import com.navisa.be.global.common.service.NationalityService;
import com.navisa.be.global.infra.embedding.GeminiEmbeddingRequestType;
import com.navisa.be.global.infra.embedding.GeminiTextEmbeddingClient;
import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ForeignerRegistrationService {

    private final GeminiTextEmbeddingClient geminiTextEmbeddingClient;
    private final JobCodeService jobCodeService;
    private final UserQueryService userQueryService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final LanguageService languageService;
    private final NationalityService nationalityService;

    /**
     * 외국인이 자신의 모든 정보를 등록/수정하고, AI가 직무 유사도를 계산하여 저장
     */
    public void registerAllForeignerInfo(ForeignerRegisterRequest request, String email) {
        List<Language> languages = languageService.findAllById(request.languageIdList());
        List<Nationality> nationalities = nationalityService.findAllById(request.nationIdList());
        validateLanguagesAndNationalities(request, languages, nationalities);

        User user = userQueryService.findByEmail(email);
        if (user.getUserType().equals(UserType.UNFILLED_FOREIGNER)) {
            user.upgradeToValidForeigner();
        }

        ForeignerProfile profile = foreignerProfileCrudService.registerForeignerTotalInfo(
                request,
                user.getId(),
                languages,
                nationalities
        );

        if (request.expectedCompany() != null && request.expectedCompany().jobTitle() != null) {
            processSimilarity(profile, request.expectedCompany().jobTitle());
        }
    }

    private void processSimilarity(ForeignerProfile profile, String jobTitle) {
        float[] embeddingResult = geminiTextEmbeddingClient.embedText(
                jobTitle,
                GeminiEmbeddingRequestType.QUERY
        );

        List<JobCodeSimilarityProjection> projectionList = jobCodeService.findTop3SimilarJobCodes(embeddingResult);

        if (projectionList.size() != 3) {
            throw new BaseException(ResponseStatus.SIMILARITY_CALCULATE_FAIL);
        }

        foreignerProfileCrudService.registerCalculatedSimilarity(profile, projectionList);
    }

    private void validateLanguagesAndNationalities(
            ForeignerRegisterRequest request,
            List<Language> languages,
            List<Nationality> nationalities
    ) {
        if (languages.size() != request.languageIdList().size()) {
            throw new BaseException(ResponseStatus.INVALID_LANGUAGE);
        }
        if (nationalities.size() != request.nationIdList().size()) {
            throw new BaseException(ResponseStatus.INVALID_NATIONALITY);
        }
    }
}
