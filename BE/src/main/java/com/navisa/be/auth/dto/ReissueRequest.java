package com.navisa.be.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
        @NotBlank(message = "리프레시 토큰이 누락되었습니다.") String refreshToken
) {}
