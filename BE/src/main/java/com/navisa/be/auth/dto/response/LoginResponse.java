package com.navisa.be.auth.dto.response;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UUID userId
) {}
