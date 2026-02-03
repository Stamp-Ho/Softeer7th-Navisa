package com.navisa.be.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "행정사 상세 조회 헤더 dto")
public record AgentDetailHeaderDto(
        @Schema(description = "상위 2개 뱃지 id")
        List<Long> top2badgeIds,
        @Schema(description = "행정사 한마디")
        String comment
) {
}
