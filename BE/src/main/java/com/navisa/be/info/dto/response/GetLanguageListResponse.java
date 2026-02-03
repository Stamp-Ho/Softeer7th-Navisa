package com.navisa.be.info.dto.response;

import com.navisa.be.info.dto.LanguageDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "언어 목록 조회 응답 dto")
public record GetLanguageListResponse(
        List<LanguageDto> languageList
) {
}
