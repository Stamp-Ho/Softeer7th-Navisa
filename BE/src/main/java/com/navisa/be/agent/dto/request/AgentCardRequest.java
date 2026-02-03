package com.navisa.be.agent.dto.request;

import java.util.List;
import java.util.Optional;

public record AgentCardRequest(
    List<String> jobGroupNameList,
    List<String> regionList,
    List<Long> languageIdList
) {
    public AgentCardRequest {
        jobGroupNameList = Optional.ofNullable(jobGroupNameList).orElse(List.of());
        regionList = Optional.ofNullable(regionList).orElse(List.of());
        languageIdList = Optional.ofNullable(languageIdList).orElse(List.of());
    }
}
