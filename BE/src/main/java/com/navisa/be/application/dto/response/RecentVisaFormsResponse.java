package com.navisa.be.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RecentVisaFormsResponse(
        UUID applicationFormId,
        String title,
        Boolean isDone,
        Integer currentStep,
        String foreignerProfileImgUrl,
        LocalDateTime lastModifiedAt
) {}
