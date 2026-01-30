package com.navisa.be.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "자격증 정보")
public record LicenseInfoDto(@Schema(description = "자격증 번호") String licenseNo,
                             @Schema(description = "자격증 발급 연월일") LocalDate licenseIssuedAt,
                             @Schema(description = "자격증 내지번호") String licenseInnerPageNo,
                             @Schema(description = "자격증 관리번호") String licenseManagementNo) {
}