package com.navisa.be.global.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "국적 목록 조회 응답 dto")
public record NationalityListResponse(
        @Schema(description = "국적 리스트")
        List<NationalityDto> nationalityList
) {
    @Schema(description = "국적 dto")
    public record NationalityDto(
            @Schema(description = "국적 id")
            Long nationalityId,
            @Schema(description = "국적 이름")
            String value
    ) {
    }
}
