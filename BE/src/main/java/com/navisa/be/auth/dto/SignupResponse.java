package com.navisa.be.auth.dto;

import com.navisa.be.user.model.enums.UserType;

import java.util.UUID;

public record SignupResponse(
        String accessToken,
        String refreshToken,
        UUID userId,
        UserType userType
) {}
