package com.navisa.be.application.dto.response;

import com.navisa.be.application.dto.projection.ApplicationFormProjection;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationFormCardResponse(
        @Schema(description = "비자 신청서 ID") UUID applicationFormId,

        @Schema(description = "신청서 제목 (신청자 이름)") String title,

        @Schema(description = "현재 진행 단계") Integer currentStep,

        @Schema(description = "총 문항 수") Integer totalCount,

        @Schema(description = "외국인 증명사진 URL") String foreignerProfileImgUrl,

        @Schema(description = "최종 수정 일시") LocalDateTime lastModifiedAt
) {
    public static ApplicationFormCardResponse projectionToDto(ApplicationFormProjection projection, String profileImgUrl) {
        return new ApplicationFormCardResponse(
                projection.id(),
                projection.nickname(),
                projection.currentStep(),
                projection.totalCount(),
                profileImgUrl,
                projection.updatedAt());
    }
}