package com.navisa.be.global.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "presigned url 요청 DTO")
public record IssuedPresignedUrlRequest(
        @Schema(description = "파일 mime 타입")
        @NotEmpty String fileMimeType,
        @Schema(description = "파일 용도")
        @NotEmpty String fileUsage
) {

}

