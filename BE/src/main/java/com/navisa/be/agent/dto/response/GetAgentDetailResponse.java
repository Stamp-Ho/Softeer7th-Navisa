package com.navisa.be.agent.dto.response;

import com.navisa.be.agent.dto.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "행정사 응답 dto")
public record GetAgentDetailResponse(
        AgentDetailHeaderDto header,
        AgentDetailExpertiseDto expertise,
        AgentDetailInfoDto agentInfo,
        @Schema(description = "행정사 추가 이력")
        String additionalHistory,
        AgentDetailReviewSummaryDto reviewSummary,
        AgentDetailOfficeInfo officeInfo
) {
}
