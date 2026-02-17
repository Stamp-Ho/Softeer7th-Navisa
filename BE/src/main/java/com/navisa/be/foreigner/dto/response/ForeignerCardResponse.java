package com.navisa.be.foreigner.dto.response;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "외국인 카드 정보")
public record ForeignerCardResponse(
        @Schema(description = "외국인 ID") UUID foreignerId,
        @Schema(description = "닉네임") String nickname,
        @Schema(description = "국적 ID 리스트") List<Long> nationIdList,
        @Schema(description = "언어 ID 리스트") List<Long> languageIdList,
        @Schema(description = "직무 타이틀") String jobTitle,
        @Schema(description = "학위 단계") EducationDegreeLevel degreeLevel
) {
    public static ForeignerCardResponse of(ForeignerProfile profile, String jobTitle, EducationDegreeLevel degreeLevel) {
        return new ForeignerCardResponse(
                profile.getId(),
                profile.getNickname(),
                profile.getForeignerNationalities().stream()
                        .map(fn -> fn.getNationality().getId()).toList(),
                profile.getForeignLanguages().stream()
                        .map(fl -> fl.getLanguage().getId()).toList(),
                jobTitle,
                degreeLevel
        );
    }
}
