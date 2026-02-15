package com.navisa.be.global.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "언어 목록 조회 응답 dto")
public record LanguageListResponse(
        List<LanguageDto> languageList
) {
    @Schema(description = "언어 dto")
    public record LanguageDto(
            @Schema(description = "언어 id")
            Long languageId,
            @Schema(description = "언어 이름")
            String value
    ) {
    }
}
