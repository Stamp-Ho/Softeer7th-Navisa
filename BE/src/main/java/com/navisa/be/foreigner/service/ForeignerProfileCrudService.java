package com.navisa.be.foreigner.service;

import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.model.entity.*;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.*;
import com.navisa.be.global.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForeignerProfileCrudService {

    private final UserCrudService userCrudService;
    private final ForeignerProfileRepository foreignerProfileRepository;
    private final ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;
    private final ForeignerNationalityRepository foreignerNationalityRepository;
    private final ForeignerLanguageRepository foreignerLanguageRepository;
    private final ForeignerSimilarityRepository foreignerSimilarityRepository;
    private final ForeignerEducationRepository foreignerEducationRepository;
    private final ForeignerCareersRepository foreignerCareersRepository;

    @Transactional
    public ForeignerProfile registerForeignerTotalInfo(
            ForeignerRegisterRequest request,
            UUID userId,
            List<Language> languages,
            List<Nationality> nationalities) {

        return foreignerProfileRepository.findByUserId(userId)
                .map(existing -> updateForeignerProfile(existing, request, languages, nationalities))
                .orElseGet(() -> createForeignerProfile(request, userId, languages, nationalities));
    }

    @Transactional
    public void registerCalculatedSimilarity(ForeignerProfile profile, List<JobCodeSimilarityProjection> projectionList) {
        foreignerSimilarityRepository.deleteByForeignerId(profile.getId());

        ForeignerSimilarity similarity = new ForeignerSimilarity(
                null,
                profile.getId(),
                projectionList.stream().mapToDouble(JobCodeSimilarityProjection::similarity).toArray(),
                projectionList.stream().mapToLong(JobCodeSimilarityProjection::id).toArray()
        );
        foreignerSimilarityRepository.save(similarity);
    }

    @Transactional
    public void syncForeignerLoginActivity(UUID userId) {
        foreignerProfileRepository.findByUserId(userId)
                .ifPresent(ForeignerProfile::updateLastLogin);
    }

    @Transactional(readOnly = true)
    public ForeignerProfile findByUserId(UUID userId) {
        return foreignerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));
    }

    @Transactional(readOnly = true)
    public ForeignerProfile findById(UUID id) {
        return foreignerProfileRepository.findById(id)
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));
    }

    @Transactional(readOnly = true)
    public UUID getForeignerIdByEmail(String email) {
        User user = userCrudService.findByEmail(email);

        ForeignerProfile profile = foreignerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        return profile.getId();
    }

    @Transactional(readOnly = true)
    public ForeignerProfile findByEmail(String loginUserEmail) {
        User loginUser = userCrudService.findByEmail(loginUserEmail);

        ForeignerProfile foreignerProfile = foreignerProfileRepository.findByUserId(loginUser.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        return foreignerProfile;
    }

    @Transactional(readOnly = true)
    public ForeignerExpectedCompany findExpectedCompanyByForeignerProfileId(UUID foreignerProfileId) {
        return foreignerExpectedCompanyRepository
                .findByForeignerId(foreignerProfileId)
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER_EXPECTEDCOMPANY));
    }

    @Transactional(readOnly = true)
    public List<Nationality> findNationalitiesByForeignerProfileId(UUID foreignerProfileId) {
        return foreignerNationalityRepository.findByForeignerProfileId(foreignerProfileId).stream()
                .map(ForeignerNationality::getNationality)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ForeignerProfile> findAllById(List<UUID> foreignerIds) {
        return foreignerProfileRepository.findAllById(foreignerIds);
    }

    @Transactional(readOnly = true)
    public ForeignerSimilarity findSimilarityByForeignerId(UUID foreignerId) {
        return foreignerSimilarityRepository.findByForeignerId(foreignerId)
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));
    }

    private ForeignerProfile createForeignerProfile(ForeignerRegisterRequest request, UUID userId,
                                                    List<Language> languages, List<Nationality> nationalities) {
        ForeignerProfile profile = request.toProfileEntity(userId);
        ForeignerProfile savedProfile = foreignerProfileRepository.save(profile);

        saveForeignerRelations(savedProfile, request, languages, nationalities);

        return savedProfile;
    }

    private ForeignerProfile updateForeignerProfile(ForeignerProfile profile, ForeignerRegisterRequest request,
                                                    List<Language> languages, List<Nationality> nationalities) {

        profile.updateStatus(request.isRequesting() ? ForeignerSearchStatus.REQUESTING : ForeignerSearchStatus.IDLE);

        updateLanguages(profile, languages);
        updateNationalities(profile, nationalities);
        updateEducation(profile, request.education());
        updateCareers(profile, request.foreignerCareers());
        updateExpectedCompany(profile, request.expectedCompany());

        return profile;
    }

    private void saveForeignerRelations(ForeignerProfile profile, ForeignerRegisterRequest request,
                                       List<Language> languages, List<Nationality> nationalities) {

        List<ForeignerLanguage> foreignerLanguages = languages.stream()
                .map(language -> new ForeignerLanguage(null, profile, language))
                .toList();
        foreignerLanguageRepository.saveAll(foreignerLanguages);

        List<ForeignerNationality> foreignerNationalities = nationalities.stream()
                .map(nationality -> new ForeignerNationality(null, profile, nationality))
                .toList();
        foreignerNationalityRepository.saveAll(foreignerNationalities);

        if (request.education() != null) {
            foreignerEducationRepository.save(request.education().toEntity(profile.getId()));
        }

        List<ForeignerCareers> foreignerCareers = request.foreignerCareers().stream()
                .map(career -> career.toEntity(profile.getId()))
                .toList();
        foreignerCareersRepository.saveAll(foreignerCareers);

        if (request.expectedCompany() != null) {
            foreignerExpectedCompanyRepository.save(request.expectedCompany().toEntity(profile.getId()));
        }
    }

    private void updateLanguages(ForeignerProfile profile, List<Language> newLanguages) {
        List<ForeignerLanguage> existingLanguages = foreignerLanguageRepository
                .findByForeignerProfileId(profile.getId());

        List<ForeignerLanguage> toDelete = existingLanguages.stream()
                .filter(existing -> newLanguages.stream()
                        .noneMatch(lang -> lang.getId().equals(existing.getLanguage().getId())))
                .toList();
        foreignerLanguageRepository.deleteAll(toDelete);

        List<ForeignerLanguage> toInsert = newLanguages.stream()
                .filter(newItem -> existingLanguages.stream()
                        .noneMatch(existing -> existing.getLanguage().getId()
                                .equals(newItem.getId())))
                .map(newItem -> new ForeignerLanguage(null, profile, newItem))
                .toList();
        foreignerLanguageRepository.saveAll(toInsert);
    }

    private void updateNationalities(ForeignerProfile profile, List<Nationality> newNationalities) {
        List<ForeignerNationality> existingNationalities = foreignerNationalityRepository
                .findByForeignerProfileId(profile.getId());

        List<ForeignerNationality> toDelete = existingNationalities.stream()
                .filter(existing -> newNationalities.stream()
                        .noneMatch(nat -> nat.getId()
                                .equals(existing.getNationality().getId())))
                .toList();
        foreignerNationalityRepository.deleteAll(toDelete);

        List<ForeignerNationality> toInsert = newNationalities.stream()
                .filter(newItem -> existingNationalities.stream()
                        .noneMatch(existing -> existing.getNationality().getId()
                                .equals(newItem.getId())))
                .map(newItem -> new ForeignerNationality(null, profile, newItem))
                .toList();
        foreignerNationalityRepository.saveAll(toInsert);
    }

    private void updateEducation(ForeignerProfile profile, ForeignerRegisterRequest.ForeignerEducationRegistration newEducation) {
        Optional<ForeignerEducation> existingEducationOpt = foreignerEducationRepository
                .findByForeignerId(profile.getId());

        if (newEducation == null) {
            existingEducationOpt.ifPresent(foreignerEducationRepository::delete);
            return;
        }

        if (existingEducationOpt.isPresent()) {
            ForeignerEducation existingEducation = existingEducationOpt.get();
            existingEducation.update(newEducation.degreeLevel(), newEducation.schoolName(),
                    newEducation.majorName());
            foreignerEducationRepository.save(existingEducation);
        } else {
            foreignerEducationRepository.save(newEducation.toEntity(profile.getId()));
        }
    }

    private void updateCareers(ForeignerProfile profile, List<ForeignerRegisterRequest.ForeignerCareerRegistration> newCareers) {
        List<ForeignerCareers> existingCareers = foreignerCareersRepository.findAllByForeignerId(profile.getId());

        List<ForeignerCareers> toDelete = existingCareers.stream()
                .filter(existing -> newCareers.stream()
                        .noneMatch(newItem -> isSameCareer(existing, newItem)))
                .toList();
        foreignerCareersRepository.deleteAll(toDelete);

        List<ForeignerCareers> toInsert = newCareers.stream()
                .filter(newItem -> existingCareers.stream()
                        .noneMatch(existing -> isSameCareer(existing, newItem)))
                .map(newItem -> newItem.toEntity(profile.getId()))
                .toList();
        foreignerCareersRepository.saveAll(toInsert);

        existingCareers.forEach(existing -> newCareers.stream()
                .filter(newItem -> isSameCareer(existing, newItem))
                .findFirst()
                .ifPresent(newItem -> {
                    existing.update(newItem.jobTitle(), newItem.endDate(), newItem.isWork());
                    foreignerCareersRepository.save(existing);
                }));
    }

    private boolean isSameCareer(ForeignerCareers entity, ForeignerRegisterRequest.ForeignerCareerRegistration dto) {
        return entity.getCompanyName().equals(dto.companyName()) &&
                entity.getStartDate().equals(dto.startDate());
    }

    private void updateExpectedCompany(ForeignerProfile profile, ForeignerRegisterRequest.ForeignerExpectedCompanyRegistration newDto) {
        Optional<ForeignerExpectedCompany> existingOpt = foreignerExpectedCompanyRepository
                .findByForeignerId(profile.getId());

        if (newDto == null) {
            existingOpt.ifPresent(foreignerExpectedCompanyRepository::delete);
            return;
        }

        if (existingOpt.isPresent()) {
            ForeignerExpectedCompany existingCompany = existingOpt.get();
            existingCompany.update(newDto.companyName(), newDto.jobTitle(), newDto.startDate());
            foreignerExpectedCompanyRepository.save(existingCompany);
        } else {
            foreignerExpectedCompanyRepository.save(newDto.toEntity(profile.getId()));
        }
    }
}
