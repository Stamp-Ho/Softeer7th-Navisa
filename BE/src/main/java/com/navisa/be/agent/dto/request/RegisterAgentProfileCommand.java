package com.navisa.be.agent.dto.request;

import com.navisa.be.agent.dto.BasicInfoDto;
import com.navisa.be.agent.dto.DetailedInfoDto;
import com.navisa.be.agent.dto.LicenseInfoDto;

public record RegisterAgentProfileCommand(BasicInfoDto basicInfo,
                                          LicenseInfoDto licenseInfo,
                                          DetailedInfoDto detailedInfo,
                                          String userEmail) {

    public RegisterAgentProfileCommand(RegisterAgentProfileRequest request, String loginUserEmail) {
        this(request.basicInfo(), request.licenseInfo(), request.detailedInfo(), loginUserEmail);
    }
}
