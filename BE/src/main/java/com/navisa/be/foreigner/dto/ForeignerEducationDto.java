package com.navisa.be.foreigner.dto;

import com.navisa.be.foreigner.model.entity.ForeignerEducation;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ForeignerEducationDto(
                @NotBlank(message = "schoolName은 빈 값일 수 없습니다.") String schoolName,
                @NotNull(message = "해당 값은 BELOW_BACHELOR, BACHELOR, ABOVE_MASTER 중에 하나여야 합니다.") EducationDegreeLevel degreeLevel,
                @NotBlank(message = "majorName은 빈 값일 수 없습니다.") String majorName) {

        public ForeignerEducation toEntity(UUID foreignerId) {
                return new ForeignerEducation(null, foreignerId, degreeLevel, schoolName, majorName);
        }
}
