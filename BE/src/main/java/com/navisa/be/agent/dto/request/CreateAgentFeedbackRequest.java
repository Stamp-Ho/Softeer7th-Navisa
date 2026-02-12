package com.navisa.be.agent.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "행정사 피드백 등록 요청")
public record CreateAgentFeedbackRequest(
        @Schema(description = "피드백 내용")
        @NotBlank(message = "피드백 내용은 비어있을 수 없습니다.")
        String content
) {}
