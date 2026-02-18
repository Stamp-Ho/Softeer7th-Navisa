package com.navisa.be.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationFormDetailResponse(
        @Schema(description = "비자 신청서 ID") UUID applicationFormId,

        @Schema(description = "외국인 증명사진 URL") String foreignerProfileImgUrl,

        @Schema(description = "작성 완료 여부") Boolean isDone,

        @Schema(description = "최종 수정 일시") LocalDateTime updatedAt,

        @Schema(description = "총 문항 수") Integer totalCount,

        @Schema(description = "작성된 문항 수") Integer filledCount,

        @Schema(description = "섹션별 데이터 목록") List<Map<String, Object>> sections
) {
}
