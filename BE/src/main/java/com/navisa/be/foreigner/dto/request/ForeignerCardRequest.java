package com.navisa.be.foreigner.dto.request;

import java.util.List;
import java.util.Optional;

public record ForeignerCardRequest(
        List<String> jobGroupNameList,
        List<Long> nationIdList,
        List<Long> languageIdList
) {
    public ForeignerCardRequest {
        jobGroupNameList = Optional.ofNullable(jobGroupNameList).orElse(List.of());
        nationIdList = Optional.ofNullable(nationIdList).orElse(List.of());
        languageIdList = Optional.ofNullable(languageIdList).orElse(List.of());
    }
}
