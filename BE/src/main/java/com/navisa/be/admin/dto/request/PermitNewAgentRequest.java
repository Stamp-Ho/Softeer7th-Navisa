package com.navisa.be.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "새로운 행정사 승인 요청 DTO")
public record PermitNewAgentRequest(
        @Schema(description = "유저 id")
        @NotNull(message = "userId는 null일 수 없습니다")
        UUID userId
) {
}
