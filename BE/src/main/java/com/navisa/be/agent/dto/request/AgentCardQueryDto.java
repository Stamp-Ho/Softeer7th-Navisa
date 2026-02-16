package com.navisa.be.agent.dto.request;

import java.util.List;
import java.util.Optional;

public record AgentCardQueryDto(
        List<Long> jobIdList,
        List<String> regionList,
        List<Long> languageIdList
) {
    public AgentCardQueryDto {
        jobIdList = Optional.ofNullable(jobIdList).orElse(List.of());
        regionList = Optional.ofNullable(regionList).orElse(List.of());
        languageIdList = Optional.ofNullable(languageIdList).orElse(List.of());
    }
}
