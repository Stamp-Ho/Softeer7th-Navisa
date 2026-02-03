package com.navisa.be.info.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "언어 dto")
public record LanguageDto(
        @Schema(description = "언어 id")
        Long languageId,
        @Schema(description = "언어 이름")
        String value
) {
}
