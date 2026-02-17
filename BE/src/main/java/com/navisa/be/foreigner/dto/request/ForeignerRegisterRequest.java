package com.navisa.be.foreigner.dto.request;

import com.navisa.be.foreigner.model.entity.ForeignerCareers;
import com.navisa.be.foreigner.model.entity.ForeignerEducation;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ForeignerRegisterRequest(
        @NotNull(message = "nationIdList는 null이면 안됩니다.") List<Long> nationIdList,
        @NotNull(message = "languageIdList는 null이면 안됩니다.") List<Long> languageIdList,
        @NotNull(message = "foreignerEducation은 null이면 안됩니다.") ForeignerEducationRegistration education,
        @NotNull(message = "foreignerCareers는 null이면 안됩니다.") List<ForeignerCareerRegistration> foreignerCareers,
        @NotNull(message = "expectedCompany는 null이면 안됩니다.") ForeignerExpectedCompanyRegistration expectedCompany,
        @NotNull(message = "isRequesting 값은 null이면 안됩니다.") Boolean isRequesting) {

    public ForeignerProfile toProfileEntity(UUID userId) {
        return new ForeignerProfile(userId, isRequesting ? ForeignerSearchStatus.REQUESTING : ForeignerSearchStatus.IDLE);
    }

    public record ForeignerEducationRegistration(
            @NotBlank(message = "schoolName은 빈 값일 수 없습니다.") String schoolName,
            @NotNull(message = "해당 값은 BELOW_BACHELOR, BACHELOR, ABOVE_MASTER 중에 하나여야 합니다.") EducationDegreeLevel degreeLevel,
            @NotBlank(message = "majorName은 빈 값일 수 없습니다.") String majorName) {

        public ForeignerEducation toEntity(UUID foreignerId) {
            return new ForeignerEducation(null, foreignerId, degreeLevel, schoolName, majorName);
        }
    }

    public record ForeignerCareerRegistration(
            @NotBlank(message = "companyName은 빈 값일 수 없습니다.") String companyName,
            @NotBlank(message = "jobTitle은 빈 값일 수 없습니다.") String jobTitle,
            @NotNull(message = "startDate는 null일 수 없습니다.") LocalDate startDate,
            LocalDate endDate,
            @NotNull(message = "isWork는 null일 수 없습니다.") Boolean isWork) {

        public ForeignerCareers toEntity(UUID foreignerId) {
            return new ForeignerCareers(null, foreignerId, companyName, jobTitle, startDate, endDate, isWork);
        }
    }

    public record ForeignerExpectedCompanyRegistration(
            @NotBlank(message = "companyName은 빈 값일 수 없습니다.") String companyName,
            @NotBlank(message = "jobTitle은 빈 값일 수 없습니다.") String jobTitle,
            @NotNull(message = "startDate는 null일 수 없습니다.") LocalDate startDate) {

        public ForeignerExpectedCompany toEntity(UUID foreignerId) {
            return new ForeignerExpectedCompany(null, foreignerId, companyName, jobTitle, startDate);
        }
    }
}
