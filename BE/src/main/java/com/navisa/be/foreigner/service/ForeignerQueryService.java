package com.navisa.be.foreigner.service;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.dto.ForeignerCareerDto;
import com.navisa.be.foreigner.dto.ForeignerEducationDto;
import com.navisa.be.foreigner.dto.ForeignerExpectedCompanyDto;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.model.entity.*;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerCareersRepository;
import com.navisa.be.foreigner.repository.ForeignerEducationRepository;
import com.navisa.be.foreigner.repository.ForeignerExpectedCompanyRepository;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ForeignerQueryService {

        private final ForeignerProfileRepository foreignerProfileRepository;
        private final ForeignerEducationRepository foreignerEducationRepository;
        private final ForeignerCareersRepository foreignerCareersRepository;
        private final ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;

        public ForeignerQueryResponse findForeignerTotalInfo(UUID userId) {
                ForeignerProfile profile = foreignerProfileRepository.findByUserIdWithNationalitiesAndLanguages(userId)
                                .orElseThrow(() -> new BaseException(ResponseStatus.INVALID_FOREIGNER));

                ForeignerEducationDto education = foreignerEducationRepository.findByForeignerId(profile.getId())
                                .orElseThrow(() -> new BaseException(ResponseStatus.INVALID_FOREIGNER)).toDto();

                List<ForeignerCareerDto> careers = foreignerCareersRepository.findByForeignerId(profile.getId())
                                .stream().map(ForeignerCareers::toDto).toList();

                ForeignerExpectedCompanyDto expectedCompany = foreignerExpectedCompanyRepository.findByForeignerId(profile.getId())
                                .orElseThrow(() -> new BaseException(ResponseStatus.INVALID_FOREIGNER)).toDto();

                List<Long> nationalityIds = profile.getForeignerNationalities().stream()
                                .map(ForeignerNationality::getNationality).map(Nationality::getId).toList();
                List<Long> languageIds = profile.getForeignLanguages().stream()
                                .map(ForeignerLanguage::getLanguage).map(Language::getId).toList();

                return new ForeignerQueryResponse(
                                nationalityIds, languageIds, education, careers, expectedCompany,
                                profile.getStatus().equals(ForeignerSearchStatus.IDLE));
        }
}
