package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentProfileRegistrationRequest;
import com.navisa.be.agent.dto.request.AgentProfileUpdateRequest;
import com.navisa.be.agent.dto.response.AgentDetailResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AgentProfileRegistrationService {

    private final UserCrudService userCrudService;
    private final AgentProfileRepository agentProfileRepository;
    private final AgentLanguageService agentLanguageService;
    private final AgentSpecializedJobService agentSpecializedJobService;
    private final AgentBadgeService agentBadgeService;
    private final StorageService storageService;
    private final AgentReviewRepository agentReviewRepository;

    @Transactional
    public AgentProfile registerAgentProfile(AgentProfileRegistrationRequest request, String loginUserEmail) {
        User user = userCrudService.findByEmail(loginUserEmail);

        validateLicenseType(request.licenseInfo());
        validateUserType(user);

        // TODO :: 최종 발표를 위해서 바로 인증된 행정사로 처리
        user.upgradeToValidAgent();

        AgentProfile savedProfile;
        try {
            savedProfile = agentProfileRepository.save(request.dtoToEntity(user));
            agentProfileRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.warn("행정사 프로필 중복 등록 시도 발생. userId {}", user.getId());
            throw new AgentException(ResponseStatus.AGENT_PROFILE_ALREADY_EXISTS);
        }

        agentSpecializedJobService.save(request.detailedInfo().specializedJobCodeIdList(), savedProfile);
        agentLanguageService.save(request.detailedInfo().availableLanguageIdList(), savedProfile);
        return savedProfile;
    }

    @Transactional
    public AgentDetailResponse updateAgentProfile(AgentProfileUpdateRequest request, String email) {
        AgentProfile agentProfile = agentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_AGENT));

        agentProfile.updateProfile(
                request.profileObjectKey(),
                request.phoneNumber(),
                request.officeName(),
                request.businessHours(),
                request.roadAddress(),
                request.officeAddressDetail(),
                request.introduction(),
                request.additionalCareer()
        );

        agentSpecializedJobService.deleteAllByAgentProfile(agentProfile);
        if (request.specializedJobCodeIdList() != null) {
            agentSpecializedJobService.save(request.specializedJobCodeIdList(), agentProfile);
        }

        agentLanguageService.deleteAllByAgentProfile(agentProfile);
        if (request.availableLanguageIdList() != null) {
            agentLanguageService.save(request.availableLanguageIdList(), agentProfile);
        }

        return getAgentDetailResponse(agentProfile);
    }

    private AgentDetailResponse getAgentDetailResponse(AgentProfile agentProfile) {
        List<AgentBadgeSummary> top6BadgeSummary = agentBadgeService.getTopKBadgeByAgentId(agentProfile.getId(), 6);

        String agentProfileImageUrl = storageService.getImgUrl(ImageSize.ORIGIN,
                agentProfile.getProfileObjectKey(), false);

        long reviewCount = agentReviewRepository.countByAgentProfileId(agentProfile.getId());

        return AgentDetailResponse.entityToDto(agentProfile, top6BadgeSummary, agentProfileImageUrl,
                Optional.empty(), reviewCount);
    }

    private void validateUserType(User user) {
        if (user.getUserType() != UserType.INVALID_AGENT) {
            throw new AgentException(ResponseStatus.NOT_ALLOWED_TO_REGISTER_AGENT_PROFILE);
        }
    }

    private void validateLicenseType(AgentProfileRegistrationRequest.LicenseInfoDto licenseInfo) {
        if (isWalletTypeLicense(licenseInfo) == isPaperTypeLicense(licenseInfo)) {
            throw new AgentException(ResponseStatus.AGENT_PROFILE_MUST_CONTAIN_ONE_TYPE_LICENSE_INFO);
        }
    }

    private boolean isPaperTypeLicense(AgentProfileRegistrationRequest.LicenseInfoDto licenseInfo) {
        return licenseInfo.licenseManagementNo() != null;
    }

    private boolean isWalletTypeLicense(AgentProfileRegistrationRequest.LicenseInfoDto licenseInfo) {
        return (licenseInfo.licenseNo() != null
                && licenseInfo.licenseIssuedAt() != null
                && licenseInfo.licenseInnerPageNo() != null);
    }
}
