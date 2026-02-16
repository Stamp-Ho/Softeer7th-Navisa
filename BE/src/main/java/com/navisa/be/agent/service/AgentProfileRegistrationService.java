package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentProfileRegistrationRequest;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AgentProfileRegistrationService {

    private final UserQueryService userQueryService;
    private final AgentProfileRepository agentProfileRepository;
    private final AgentLanguageService agentLanguageService;
    private final AgentSpecializedJobService agentSpecializedJobService;

    @Transactional
    public AgentProfile registerAgentProfile(AgentProfileRegistrationRequest request, String loginUserEmail) {
        User user = userQueryService.findByEmail(loginUserEmail);

        validateLicenseType(request.licenseInfo());

        if (user.getUserType() != UserType.INVALID_AGENT) {
            throw new AgentException(ResponseStatus.NOT_ALLOWED_TO_REGISTER_AGENT_PROFILE);
        }
        AgentProfile savedProfile = agentProfileRepository.save(request.dtoToEntity(user));
        agentSpecializedJobService.save(request.detailedInfo().specializedJobCodeIdList(), savedProfile);
        agentLanguageService.save(request.detailedInfo().availableLanguageIdList(), savedProfile);
        return savedProfile;
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
