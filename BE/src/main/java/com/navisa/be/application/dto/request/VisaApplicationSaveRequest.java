package com.navisa.be.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.Map;

public record VisaApplicationSaveRequest(
        @PositiveOrZero int totalCount,
        @PositiveOrZero int filledCount,
        @NotNull List<Map<String, Object>> sections
) {}
