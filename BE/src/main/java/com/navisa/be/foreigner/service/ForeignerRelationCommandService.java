package com.navisa.be.foreigner.service;

import com.navisa.be.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.foreigner.dto.ForeignerCareerDto;
import com.navisa.be.foreigner.dto.ForeignerEducationDto;
import com.navisa.be.foreigner.dto.ForeignerExpectedCompanyDto;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.model.entity.*;
import com.navisa.be.foreigner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ForeignerRelationCommandService {

    private final ForeignerLanguageRepository foreignerLanguageRepository;
    private final ForeignerNationalityRepository foreignerNationalityRepository;
    private final ForeignerEducationRepository foreignerEducationRepository;
    private final ForeignerCareersRepository foreignerCareersRepository;
    private final ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;
    private final ForeignerSimilarityRepository foreignerSimilarityRepository;

    public void saveForeignerRelations(ForeignerProfile profile, ForeignerRegisterRequest request,
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

    public void updateLanguages(ForeignerProfile profile, List<Language> newLanguages) {
        List<ForeignerLanguage> existingLanguages = foreignerLanguageRepository
                .findByForeignerProfileId(profile.getId());

        // Delete missing
        List<ForeignerLanguage> toDelete = existingLanguages.stream()
                .filter(existing -> newLanguages.stream()
                        .noneMatch(lang -> lang.getId().equals(existing.getLanguage().getId())))
                .toList();
        foreignerLanguageRepository.deleteAll(toDelete);

        // Insert new
        List<ForeignerLanguage> toInsert = newLanguages.stream()
                .filter(newItem -> existingLanguages.stream()
                        .noneMatch(existing -> existing.getLanguage().getId()
                                .equals(newItem.getId())))
                .map(newItem -> new ForeignerLanguage(null, profile, newItem))
                .toList();
        foreignerLanguageRepository.saveAll(toInsert);
    }

    public void updateNationalities(ForeignerProfile profile, List<Nationality> newNationalities) {
        List<ForeignerNationality> existingNationalities = foreignerNationalityRepository
                .findByForeignerProfileId(profile.getId());

        // Delete missing
        List<ForeignerNationality> toDelete = existingNationalities.stream()
                .filter(existing -> newNationalities.stream()
                        .noneMatch(nat -> nat.getId()
                                .equals(existing.getNationality().getId())))
                .toList();
        foreignerNationalityRepository.deleteAll(toDelete);

        // Insert new
        List<ForeignerNationality> toInsert = newNationalities.stream()
                .filter(newItem -> existingNationalities.stream()
                        .noneMatch(existing -> existing.getNationality().getId()
                                .equals(newItem.getId())))
                .map(newItem -> new ForeignerNationality(null, profile, newItem))
                .toList();
        foreignerNationalityRepository.saveAll(toInsert);
    }

    public void updateEducation(ForeignerProfile profile, ForeignerEducationDto newEducationDto) {
        Optional<ForeignerEducation> existingEducationOpt = foreignerEducationRepository
                .findByForeignerId(profile.getId());

        if (newEducationDto == null) {
            existingEducationOpt.ifPresent(foreignerEducationRepository::delete);
            return;
        }

        if (existingEducationOpt.isPresent()) {
            ForeignerEducation existingEducation = existingEducationOpt.get();
            existingEducation.update(newEducationDto.degreeLevel(), newEducationDto.schoolName(),
                    newEducationDto.majorName());
            foreignerEducationRepository.save(existingEducation);
        } else {
            foreignerEducationRepository.save(newEducationDto.toEntity(profile.getId()));
        }
    }

    public void updateCareers(ForeignerProfile profile, List<ForeignerCareerDto> newCareers) {
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

    private boolean isSameCareer(ForeignerCareers entity, ForeignerCareerDto dto) {
        return entity.getCompanyName().equals(dto.companyName()) &&
                entity.getStartDate().equals(dto.startDate());
    }

    public void updateExpectedCompany(ForeignerProfile profile, ForeignerExpectedCompanyDto newDto) {
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

    public void registerCalculatedSimilarity(ForeignerProfile profile,
            List<JobCodeSimilarityProjection> projectionList) {

        // 기존 유사도 정보 삭제
        foreignerSimilarityRepository.deleteByForeignerId(profile.getId());

        // 1. Long(객체) -> long(기본형) 배열 변환
        long[] jobCodeIdList = projectionList.stream()
                .mapToLong(JobCodeSimilarityProjection::getId)
                .toArray();

        // 2. Double(객체) -> double(기본형) 배열 변환
        double[] similarityList = projectionList.stream()
                .mapToDouble(JobCodeSimilarityProjection::getSimilarity)
                .toArray();

        // 3. Entity 생성 및 저장
        ForeignerSimilarity similarity = new ForeignerSimilarity(null,
                profile.getId(), similarityList, jobCodeIdList);

        foreignerSimilarityRepository.save(similarity);
    }
}
