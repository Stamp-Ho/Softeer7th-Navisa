package com.navisa.be.auth.dto.request;

public record LogoutRequest(
        String refreshToken
) {}
