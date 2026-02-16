package com.navisa.be.agent.dto.response;

import java.util.List;

public record ReviewReliabilityResponse(
        List<Long> top3JobIds,
        List<Double> relativeRatios) {

}
