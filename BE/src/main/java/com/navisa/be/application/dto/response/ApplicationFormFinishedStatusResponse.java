package com.navisa.be.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationFormFinishedStatusResponse(
        @Schema(description = "종료된 비자 신청서 ID") UUID closedVisaFormId,

        @Schema(description = "새로 생성된(복사된) 비자 신청서 ID") UUID newVisaFormId,

        @Schema(description = "처리 일시") LocalDateTime updatedAt
) {}
