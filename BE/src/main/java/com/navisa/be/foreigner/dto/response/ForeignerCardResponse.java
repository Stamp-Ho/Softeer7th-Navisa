package com.navisa.be.foreigner.dto.response;

import com.navisa.be.foreigner.model.entity.ForeignerNationality;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;

import java.util.List;
import java.util.UUID;

public record ForeignerCardResponse(
        UUID foreignerId,
        String nickname,
        List<Long> nationIdList,
        List<Long> languageIdList,
        String jobTitle
) {

    public static ForeignerCardResponse toDto(ForeignerProfile foreignerProfile, String jobTitle) {
        return new ForeignerCardResponse(
                foreignerProfile.getId(),
                foreignerProfile.getNickname(),
                foreignerProfile.getForeignerNationalities().stream().map(
                        foreignerNationality -> foreignerNationality.getNationality().getId()
                ).toList(),
                foreignerProfile.getForeignLanguages().stream().map(
                        foreignerLanguage -> foreignerLanguage.getLanguage().getId()
                ).toList(),
                jobTitle
        );
    }
}
