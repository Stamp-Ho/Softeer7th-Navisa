package com.navisa.be.application.dto.response;

import com.navisa.be.application.dto.projection.VisaApplicationFormProjection;

import java.time.LocalDateTime;
import java.util.UUID;

public record VisaApplicationCardResponse(
        UUID applicationFormId,
        String title,
        Integer currentStep,
        Integer totalCount,
        String foreignerProfileImgUrl,
        LocalDateTime lastModifiedAt
) {
    public static VisaApplicationCardResponse projectionToDto(VisaApplicationFormProjection projection, String profileImgUrl) {
        return new VisaApplicationCardResponse(
                projection.id(),
                projection.nickname(),
                projection.currentStep(),
                projection.totalCount(),
                profileImgUrl,
                projection.updatedAt()
        );
    }
}