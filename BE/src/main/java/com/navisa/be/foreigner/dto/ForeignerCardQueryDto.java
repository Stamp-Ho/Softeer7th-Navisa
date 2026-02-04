package com.navisa.be.foreigner.dto;

import java.util.List;
import java.util.Optional;

public record ForeignerCardQueryDto(
        List<Long> jobIdList,
        List<Long> nationIdList,
        List<Long> languageIdList
) {
    public ForeignerCardQueryDto {
        jobIdList = Optional.ofNullable(jobIdList).orElse(List.of());
        nationIdList = Optional.ofNullable(nationIdList).orElse(List.of());
        languageIdList = Optional.ofNullable(languageIdList).orElse(List.of());
    }
}
