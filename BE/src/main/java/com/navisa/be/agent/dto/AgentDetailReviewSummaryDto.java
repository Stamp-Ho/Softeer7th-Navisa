package com.navisa.be.agent.dto;

import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "행정사 리뷰 요약 dto")
public record AgentDetailReviewSummaryDto(
        @Schema(description = "행정사 총 리뷰 수")
        Long totalCount,
        @Schema(description = "행정사 상위 6개 배지")
        List<AgentDetailBadge> strengths
) {
    public static AgentDetailReviewSummaryDto entityToDto(long reviewCount, List<AgentBadgeSummary> badgeSummarys) {
        if (badgeSummarys == null) {
            return new AgentDetailReviewSummaryDto(reviewCount, List.of());
        }

        List<AgentDetailReviewSummaryDto.AgentDetailBadge> top6badges = badgeSummarys.stream()
                .map(summary -> new AgentDetailReviewSummaryDto.AgentDetailBadge(
                        summary.getBadge().getId(), summary.getCount()))
                .toList();
        return new AgentDetailReviewSummaryDto(reviewCount, top6badges);
    }

    public record AgentDetailBadge(
            @Schema(description = "배지 id")
            Long badgeId,
            @Schema(description = "배지 수")
            Integer badgeCount
    ) {
    }
}
