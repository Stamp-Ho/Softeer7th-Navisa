package com.navisa.be.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ApplicationStatusUpdateRequest(
        @NotNull(message = "상태값(isDone)은 필수입니다.")
        Boolean isDone
) {}
