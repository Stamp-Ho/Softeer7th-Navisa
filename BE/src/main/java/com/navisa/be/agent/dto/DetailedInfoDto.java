package com.navisa.be.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "세부 정보")
public record DetailedInfoDto(@Schema(description = "특화 직무 코드 id 리스트") @NotNull List<Long> specializedJobCodeIdList,
                              @Schema(description = "사용 가능 언어 id 리스트") @NotNull List<Long> availableLanguageIdList,
                              @Schema(description = "행정사 한마디") @NotBlank String agentComment,
                              @Schema(description = "추가 이력") String additionalHistory) {
}