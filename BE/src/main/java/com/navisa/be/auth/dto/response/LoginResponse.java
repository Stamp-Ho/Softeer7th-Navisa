package com.navisa.be.auth.dto.response;

import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record LoginResponse(
        @Schema(description = "accessToken 입니다.")
        String accessToken,
        @Schema(description = "userId 입니다.")
        UUID userId,
        @Schema(description = "userType 입니다.")
        UserType userType
) {}
