package com.navisa.be.application.dto.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationFormProjection(
        UUID id,
        String nickname,
        Integer currentStep,
        Integer totalCount,
        String profileObjectKey,
        LocalDateTime updatedAt
) {
}
