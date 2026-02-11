package com.navisa.be.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record VisaApplicationDetailResponse(
        UUID applicationFormId,
        String foreignerProfileImgUrl,
        Boolean isDone,
        LocalDateTime updatedAt,
        Integer totalCount,
        Integer filledCount,
        List<Map<String, Object>> sections
) {
}
