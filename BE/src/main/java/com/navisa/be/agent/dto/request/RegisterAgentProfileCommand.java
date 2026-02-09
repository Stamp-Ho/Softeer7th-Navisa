package com.navisa.be.agent.dto.request;

import com.navisa.be.agent.dto.AgentBasicInfoDto;
import com.navisa.be.agent.dto.DetailedInfoDto;
import com.navisa.be.agent.dto.LicenseInfoDto;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.user.model.entity.User;

public record RegisterAgentProfileCommand(AgentBasicInfoDto basicInfo,
                                          LicenseInfoDto licenseInfo,
                                          DetailedInfoDto detailedInfo,
                                          String userEmail) {

    public RegisterAgentProfileCommand(RegisterAgentProfileRequest request, String loginUserEmail) {
        this(request.basicInfo(), request.licenseInfo(), request.detailedInfo(), loginUserEmail);
    }

    public AgentProfile dtoToEntity(User user) {
        return new AgentProfile(basicInfo().agentName(),
                basicInfo().birthDate(),
                basicInfo().profileImageUrl(),
                basicInfo().businessTime(),
                basicInfo().officeName(),
                basicInfo().officeAddress(),
                basicInfo().officeAddressDetail(),
                detailedInfo().additionalHistory(),
                basicInfo().phoneNumber(),
                user.getId(),
                licenseInfo().licenseNo(),
                licenseInfo().licenseIssuedAt(),
                licenseInfo().licenseInnerPageNo(),
                licenseInfo().licenseManagementNo(),
                detailedInfo().agentComment()
        );
    }
}
