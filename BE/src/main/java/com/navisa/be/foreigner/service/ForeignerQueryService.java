package com.navisa.be.foreigner.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileQueryService;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.entity.Nationality;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.dto.ForeignerCardQueryDto;
import com.navisa.be.foreigner.dto.ForeignerCareerDto;
import com.navisa.be.foreigner.dto.ForeignerEducationDto;
import com.navisa.be.foreigner.dto.ForeignerExpectedCompanyDto;
import com.navisa.be.foreigner.dto.response.ForeignerCardExtensionResponse;
import com.navisa.be.foreigner.dto.response.ForeignerCardResponse;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.model.entity.*;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerCareersRepository;
import com.navisa.be.foreigner.repository.ForeignerEducationRepository;
import com.navisa.be.foreigner.repository.ForeignerExpectedCompanyRepository;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import com.navisa.be.user.service.UserQueryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ForeignerQueryService {

    private final ForeignerProfileRepository foreignerProfileRepository;
    private final ForeignerEducationRepository foreignerEducationRepository;
    private final ForeignerCareersRepository foreignerCareersRepository;
    private final ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;
    private final UserRepository userRepository;
    private final UserQueryService userQueryService;
    private final AgentProfileQueryService agentProfileQueryService;

    public ForeignerQueryResponse findForeignerTotalInfo(UUID userId) {
        ForeignerProfile profile = foreignerProfileRepository.findByUserIdWithNationalitiesAndLanguages(userId)
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        ForeignerEducationDto education = foreignerEducationRepository.findByForeignerId(profile.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER)).toDto();

        List<ForeignerCareerDto> careers = foreignerCareersRepository.findByForeignerId(profile.getId())
                .stream().map(ForeignerCareers::toDto).toList();

        ForeignerExpectedCompanyDto expectedCompany = foreignerExpectedCompanyRepository.findByForeignerId(profile.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER)).toDto();

        List<Long> nationalityIds = profile.getForeignerNationalities().stream()
                .map(ForeignerNationality::getNationality).map(Nationality::getId).toList();
        List<Long> languageIds = profile.getForeignLanguages().stream()
                .map(ForeignerLanguage::getLanguage).map(Language::getId).toList();

        return new ForeignerQueryResponse(
                nationalityIds, languageIds, education, careers, expectedCompany,
                profile.getStatus().equals(ForeignerSearchStatus.IDLE));
    }

    // 외국인 상세 요건 입력 여부 확인
    public ForeignerStatusResponse checkForeignerFilledStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ForeignerException(ResponseStatus.USER_INVALID));

        ForeignerProfile profile = foreignerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        boolean isCompletedRecommendation = (user.getUserType() == UserType.FILLED_FOREIGNER);

        return new ForeignerStatusResponse(
                profile.getId(),
                isCompletedRecommendation
        );
    }

    public List<ForeignerCardResponse> findForeignerCardMatchOnSpecializedJob(String email) {
        User findUser = userQueryService.findByEmail(email);

        AgentProfile profile = agentProfileQueryService.findWithSpecializedJobByUserId(findUser.getId());

        long[] agentSpecializedJobIds = profile.getSpecializedJobs().stream()
                .map(agentSpecializedJob -> agentSpecializedJob.getJobCode().getId())
                .mapToLong(Long::longValue)
                .toArray();

        List<ForeignerProfile> foreignerProfiles =
                foreignerProfileRepository.findTop10ByJobCodeMatching(agentSpecializedJobIds);

        if (foreignerProfiles.isEmpty())
            return List.of(); // 매칭되는 foreigner가 없는 경우, 빈 배열을 받도록 수정

        List<UUID> foreignerProfileIds = foreignerProfiles.stream().map(ForeignerProfile::getId).toList();

        Map<UUID, String> expectedCompanyMap = convertToMapAndValidate(
                foreignerExpectedCompanyRepository.findAllByForeignerIdIn(foreignerProfileIds),
                foreignerProfiles,
                ForeignerExpectedCompany::getForeignerId,
                ForeignerExpectedCompany::getJobTitle,
                ResponseStatus.INVALID_FOREIGNER_EXPECTEDCOMPANY
        );

        return foreignerProfiles.stream()
                .map(fp -> ForeignerCardResponse.of(fp, expectedCompanyMap.get(fp.getId())))
                .toList();
    }

    public SliceResponse<ForeignerCardExtensionResponse, UUID> findForeignerProfileCardsBasedOnFilter(ForeignerCardQueryDto dto, SliceRequest<UUID> slice) {

        List<ForeignerProfile> foreignerProfiles = foreignerProfileRepository.findByFilters(dto, slice);

        boolean existsNext = foreignerProfiles.size() > slice.size();

        List<ForeignerProfile> contentProfiles = existsNext
                ? foreignerProfiles.subList(0, slice.size())
                : foreignerProfiles;

        List<UUID> foreignerProfileIds = contentProfiles.stream().map(ForeignerProfile::getId).toList();

        Map<UUID, String> expectedCompanyMap = convertToMapAndValidate(
                foreignerExpectedCompanyRepository.findAllByForeignerIdIn(foreignerProfileIds),
                contentProfiles,
                ForeignerExpectedCompany::getForeignerId,
                ForeignerExpectedCompany::getJobTitle,
                ResponseStatus.INVALID_FOREIGNER_EXPECTEDCOMPANY
        );

        Map<UUID, EducationDegreeLevel> educationMap = convertToMapAndValidate(
                foreignerEducationRepository.findAllByForeignerIdIn(foreignerProfileIds),
                contentProfiles,
                ForeignerEducation::getForeignerId,
                ForeignerEducation::getDegreeLevel,
                ResponseStatus.INVALID_FOREIGNER_EDUCATION
        );

        UUID lastElementId = contentProfiles.isEmpty() ? null : contentProfiles.get(contentProfiles.size() - 1).getId();

        return new SliceResponse<>(
                foreignerProfiles.stream()
                    .map(fp ->
                            ForeignerCardExtensionResponse.of(
                                    fp,
                                    expectedCompanyMap.get(fp.getId()),
                                    educationMap.get(fp.getId())))
                    .toList(),
                existsNext,
                lastElementId);
    }

    private <T, R> Map<UUID, R> convertToMapAndValidate(
            List<T> sourceList,
            List<ForeignerProfile> profiles,
            Function<T, UUID> keyExtractor,
            Function<T, R> valueExtractor,
            ResponseStatus status
    ) {
        // 1. 리스트를 Map으로 변환 (중복 시 기존 값 유지)
        Map<UUID, R> resultMap = sourceList.stream()
                .collect(Collectors.toMap(
                        keyExtractor,
                        valueExtractor,
                        (existing, replacement) -> existing
                ));

        // 2. 모든 프로필 ID가 Map에 포함되어 있는지 검증
        boolean allMatched = profiles.stream()
                .map(ForeignerProfile::getId)
                .allMatch(resultMap::containsKey);

        if (!allMatched) {
            throw new ForeignerException(status);
        }

        return resultMap;
    }
}
