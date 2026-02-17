package com.navisa.be.foreigner.dto.response;

import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "확장된 외국인 카드 정보 (학위 포함)")
public record ForeignerCardExtensionResponse(
        @Schema(description = "외국인 ID") UUID foreignerId,
        @Schema(description = "닉네임") String nickname,
        @Schema(description = "국적 ID 리스트") List<Long> nationIdList,
        @Schema(description = "보유 언어 ID 리스트") List<Long> languageIdList,
        @Schema(description = "직무 타이틀") String jobTitle,
        @Schema(description = "학위 단계") EducationDegreeLevel degreeLevel
) {
    public static ForeignerCardExtensionResponse of(
            ForeignerProfile foreignerProfile,
            String jobTitle,
            EducationDegreeLevel degreeLevel
    ) {
        return new ForeignerCardExtensionResponse(
                foreignerProfile.getId(),
                foreignerProfile.getNickname(),
                foreignerProfile.getForeignerNationalities().stream()
                        .map(fn -> fn.getNationality().getId())
                        .toList(),
                foreignerProfile.getForeignLanguages().stream()
                        .map(fl -> fl.getLanguage().getId())
                        .toList(),
                jobTitle,
                degreeLevel
        );
    }
}
