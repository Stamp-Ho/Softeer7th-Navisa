package com.navisa.be.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "행정사 사무소 정보 dto")
public record AgentDetailOfficeInfo(
        @Schema(description = "사무실 이름")
        String officeName,
        @Schema(description = "사무실 주소")
        String address,
        @Schema(description = "영업시간")
        String businessHours,
        @Schema(description = "전화번호")
        String phoneNumber
) {
}
