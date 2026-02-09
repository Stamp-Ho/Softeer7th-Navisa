package com.navisa.be.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "기본 정보")
public record AgentBasicInfoDto(@Schema(description = "프로필 이미지") @NotBlank String profileImageUrl,
                                @Schema(description = "행정사 이름") @NotBlank String agentName,
                                @Schema(description = "생년월일") @NotNull LocalDate birthDate,
                                @Schema(description = "사무실 이름") @NotBlank String officeName,
                                @Schema(description = "사무실 주소") @NotBlank String officeAddress,
                                @Schema(description = "사무실 세부 주소") @NotNull String officeAddressDetail,
                                @Schema(description = "행정사 영업시간") @NotBlank String businessTime,
                                @Schema(description = "행정사 전화번호") @NotBlank String phoneNumber) {
}
