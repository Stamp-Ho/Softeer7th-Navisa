package com.navisa.be.auth.dto.response;

import com.navisa.be.user.model.enums.UserType;

import java.util.UUID;

public record SignupResponse(
        String accessToken,
        UUID userId,
        UserType userType
) {}
