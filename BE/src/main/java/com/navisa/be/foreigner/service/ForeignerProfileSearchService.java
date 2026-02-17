package com.navisa.be.foreigner.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.foreigner.dto.request.ForeignerCardQuery;
import com.navisa.be.foreigner.dto.request.ForeignerCardRequest;
import com.navisa.be.foreigner.dto.response.ForeignerCardExtensionResponse;
import com.navisa.be.foreigner.dto.response.ForeignerCardResponse;
import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.model.entity.ForeignerEducation;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.foreigner.repository.ForeignerEducationRepository;
import com.navisa.be.foreigner.repository.ForeignerExpectedCompanyRepository;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.global.common.service.JobGroupService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ForeignerProfileSearchService {

    private final UserQueryService userQueryService;
    private final AgentProfileCrudService agentProfileCrudService;
    private final JobGroupService jobGroupService;
    private final ForeignerProfileRepository foreignerProfileRepository;
    private final ForeignerExpectedCompanyRepository foreignerExpectedCompanyRepository;
    private final ForeignerEducationRepository foreignerEducationRepository;

    public List<ForeignerCardResponse> findForeignerCardMatchOnSpecializedJob(String email) {
        User findUser = userQueryService.findByEmail(email);

        AgentProfile profile = agentProfileCrudService.findWithSpecializedJobByUserId(findUser.getId());

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

        Map<UUID, EducationDegreeLevel> educationMap = foreignerEducationRepository.findAllByForeignerIdIn(foreignerProfileIds)
                .stream()
                .collect(Collectors.toMap(
                        ForeignerEducation::getForeignerId,
                        ForeignerEducation::getDegreeLevel
                ));

        return foreignerProfiles.stream()
                .map(fp -> ForeignerCardResponse.of(fp, expectedCompanyMap.get(fp.getId()), educationMap.get(fp.getId())))
                .toList();
    }

    public SliceResponse<ForeignerCardExtensionResponse, UUID> findForeignerProfileCardsBasedOnFilter(ForeignerCardRequest request, SliceRequest<UUID> slice) {

        List<Long> jobCodeIds = jobGroupService.findAllJobCodeIdsByGroupNames(request.jobGroupNameList());

        ForeignerCardQuery queryDto = new ForeignerCardQuery(
                jobCodeIds,
                request.nationIdList(),
                request.languageIdList()
        );

        List<ForeignerProfile> foreignerProfiles = foreignerProfileRepository.findByFilters(queryDto, slice);

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
                contentProfiles.stream()
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
