package com.navisa.be.agent.dto.request;

import com.navisa.be.agent.dto.AgentBasicInfoDto;
import com.navisa.be.agent.dto.DetailedInfoDto;
import com.navisa.be.agent.dto.LicenseInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(description = "행정사 등록 API 요청 DTO")
public record RegisterAgentProfileRequest(@Valid @NotNull(message = "null일 수 없습니다") AgentBasicInfoDto basicInfo,
                                          @Valid @NotNull(message = "null일 수 없습니다") LicenseInfoDto licenseInfo,
                                          @Valid @NotNull(message = "null일 수 없습니다") DetailedInfoDto detailedInfo) {


}

