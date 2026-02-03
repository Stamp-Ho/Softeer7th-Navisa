package com.navisa.be.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "행정사 리뷰 요약 dto")
public record AgentDetailReviewSummaryDto(
        @Schema(description = "행정사 총 리뷰 수")
        Long totalCount,
        @Schema(description = "행정사 상위 6개 배지")
        List<AgentDetailBadge> strengths
) {
    public record AgentDetailBadge(
            @Schema(description = "배지 id")
            Long badgeId,
            @Schema(description = "배지 수")
            Integer badgeCount
    ) {
    }
}
