package com.navisa.be.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisaApplicationSaveResponse (
        UUID visaFormId,
        LocalDateTime updatedAt
) {
}
