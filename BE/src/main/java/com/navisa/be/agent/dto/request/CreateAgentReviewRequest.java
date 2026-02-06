package com.navisa.be.agent.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

@Schema(description = "리뷰 생성 요청 dto")
public record CreateAgentReviewRequest(
        @Schema(description = "뱃지 id 리스트")
        @NotNull(message = "뱃지 id 리스트는 Null일 수 없습니다")
        @Size(min = 1, max = 3, message = "뱃지 id 리스트는 길이가 1~3이어야 합니다")
        List<Long> badgeIdList,
        @Schema(description = "리뷰를 남길 행정사 id")
        @NotNull(message = "행정사 id는 Null일 수 없습니다")
        UUID agentId
) {

}
