package com.navisa.be.auth.dto;

import com.navisa.be.user.model.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GoogleLoginRequest(
        @NotBlank(message = "ID 토큰은 필수입니다.") String idToken,
        @NotNull(message = "유저 타입은 필수입니다.") UserType userType
) {}
