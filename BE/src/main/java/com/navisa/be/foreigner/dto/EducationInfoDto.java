package com.navisa.be.foreigner.dto;

import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "최종 학력 dto")
public record EducationInfoDto(
        @Schema(description = "학위 단계")
        EducationDegreeLevel degreeLevel,
        @Schema(description = "대학명")
        String school,
        @Schema(description = "전공명")
        String major
) {
}
