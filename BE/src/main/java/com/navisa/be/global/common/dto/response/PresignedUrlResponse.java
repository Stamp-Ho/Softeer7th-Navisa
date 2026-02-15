package com.navisa.be.global.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "presigned url 응답 DTO")
public record PresignedUrlResponse(
        @Schema(description = "presigned url")
        String url,
        @Schema(description = "버킷 객체 오브젝트 키")
        String objectKey
) {

}
