package com.navisa.be.auth.dto;

public record LogoutRequest(
        String refreshToken
) {}
