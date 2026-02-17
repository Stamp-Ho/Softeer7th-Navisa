package com.navisa.be.foreigner.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

@Schema(description = "외국인 카드 목록 조회를 위한 필터 쿼리")
public record ForeignerCardQuery(
        @Schema(description = "희망 직무 ID 리스트", example = "[1, 2, 3]")
        List<Long> jobIdList,

        @Schema(description = "국적 ID 리스트", example = "[10, 20]")
        List<Long> nationIdList,

        @Schema(description = "언어 ID 리스트", example = "[5, 6]")
        List<Long> languageIdList
) {
    public ForeignerCardQuery {
        jobIdList = Optional.ofNullable(jobIdList).orElse(List.of());
        nationIdList = Optional.ofNullable(nationIdList).orElse(List.of());
        languageIdList = Optional.ofNullable(languageIdList).orElse(List.of());
    }
}
