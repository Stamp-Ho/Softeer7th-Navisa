package com.navisa.be.foreigner.dto.response;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class ForeignerCardExtensionResponse extends ForeignerCardResponse {

    private final EducationDegreeLevel degreeLevel;

    // 부모 클래스의 필드까지 모두 받아 super()로 넘겨주는 생성자가 필요합니다.
    public ForeignerCardExtensionResponse(UUID foreignerId, String nickname, List<Long> nationIdList,
                                          List<Long> languageIdList, String jobTitle,
                                          EducationDegreeLevel degreeLevel) {

        super(foreignerId, nickname, nationIdList, languageIdList, jobTitle);
        this.degreeLevel = degreeLevel;
    }

    // 정적 팩토리 메서드
    public static ForeignerCardExtensionResponse of(ForeignerProfile foreignerProfile, String jobTitle, EducationDegreeLevel degreeLevel) {
        return new ForeignerCardExtensionResponse(
                foreignerProfile.getId(),
                foreignerProfile.getNickname(),
                foreignerProfile.getForeignerNationalities().stream()
                        .map(fn -> fn.getNationality().getId()).toList(),
                foreignerProfile.getForeignLanguages().stream()
                        .map(fl -> fl.getLanguage().getId()).toList(),
                jobTitle,
                degreeLevel
        );
    }
}
