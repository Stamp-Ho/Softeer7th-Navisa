package com.navisa.be.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ApplicationFormFinishedStatusRequest(
        @NotNull(message = "종료 상태값(isFinished)은 필수입니다.")
        Boolean isFinished
) {}
