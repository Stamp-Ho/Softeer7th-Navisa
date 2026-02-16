package com.navisa.be.agent.dto.projection;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "직무코드 DTO")
public record JobCodeProjection(
        @Schema(description = "직무코드 id") Long jobCodeId,
        @Schema(description = "직무코드 이름") String name,
        @Schema(description = "직무코드 코드네임") String code
) {
}
