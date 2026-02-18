package com.navisa.be.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationFormIdResponse(
        @Schema(description = "비자 신청서 ID") UUID visaFormId,

        @Schema(description = "수정 일시") LocalDateTime updatedAt
) {
}
