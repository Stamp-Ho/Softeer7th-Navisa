package com.navisa.be.application.dto.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisaApplicationFormProjection(
        UUID id,
        String nickname,
        Integer currentStep,
        String profileObjectKey,
        LocalDateTime updatedAt
) {
}
