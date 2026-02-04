package com.navisa.be.foreigner.dto.response;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ForeignerCardResponse {

    private final UUID foreignerId;
    private final String nickname;
    private final List<Long> nationIdList;
    private final List<Long> languageIdList;
    private final String jobTitle;

    public static ForeignerCardResponse of(ForeignerProfile foreignerProfile, String jobTitle) {
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
