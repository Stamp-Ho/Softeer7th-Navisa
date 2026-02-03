package com.navisa.be.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "행정사 전문분야 dto")
public record AgentDetailExpertiseDto(
        @Schema(description = "행정사 특화직무 id")
        List<Long> jobCodeIds,
        @Schema(description = "행정사 사용가능 언어 id")
        List<Long> languageIds
) {
}
