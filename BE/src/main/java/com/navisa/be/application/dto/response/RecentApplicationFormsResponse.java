package com.navisa.be.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecentApplicationFormsResponse(
        @Schema(description = "비자 신청서 ID") UUID applicationFormId,

        @Schema(description = "신청서 제목") String title,

        @Schema(description = "작성 완료 여부") Boolean isDone,

        @Schema(description = "현재 진행 단계") Integer currentStep,

        @Schema(description = "총 입력 필드 수") Integer totalCount,

        @Schema(description = "외국인 증명사진 URL") String foreignerProfileImgUrl,

        @Schema(description = "최종 수정 일시") LocalDateTime lastModifiedAt
) {}
