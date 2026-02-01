package com.navisa.be.auth.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
