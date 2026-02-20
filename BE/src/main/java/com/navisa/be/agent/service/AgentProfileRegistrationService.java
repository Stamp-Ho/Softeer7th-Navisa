package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentProfileRegistrationRequest;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AgentProfileRegistrationService {

    private final UserCrudService userCrudService;
    private final AgentProfileRepository agentProfileRepository;
    private final AgentLanguageService agentLanguageService;
    private final AgentSpecializedJobService agentSpecializedJobService;

    @Transactional
    public AgentProfile registerAgentProfile(AgentProfileRegistrationRequest request, String loginUserEmail) {
        User user = userCrudService.findByEmail(loginUserEmail);

        validateLicenseType(request.licenseInfo());
        validateUserType(user);

        // TODO :: 최종 발표를 위해서 바로 인증된 행정사로 처리
        user.upgradeToValidAgent();

        AgentProfile savedProfile;
        try{
            savedProfile = agentProfileRepository.save(request.dtoToEntity(user));
            agentProfileRepository.flush();
        }
        catch(DataIntegrityViolationException e){
            log.warn("행정사 프로필 중복 등록 시도 발생. userId {}", user.getId());
            throw new AgentException(ResponseStatus.AGENT_PROFILE_ALREADY_EXISTS);
        }

        agentSpecializedJobService.save(request.detailedInfo().specializedJobCodeIdList(), savedProfile);
        agentLanguageService.save(request.detailedInfo().availableLanguageIdList(), savedProfile);
        return savedProfile;
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
