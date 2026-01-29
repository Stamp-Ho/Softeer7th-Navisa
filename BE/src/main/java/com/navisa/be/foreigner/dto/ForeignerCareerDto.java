package com.navisa.be.foreigner.dto;

import com.navisa.be.foreigner.model.entity.ForeignerCareers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ForeignerCareerDto(
                @NotBlank(message = "companyName은 빈 값일 수 없습니다.") String companyName,
                @NotBlank(message = "jobTitle은 빈 값일 수 없습니다.") String jobTitle,
                @NotNull(message = "startDate는 null일 수 없습니다.") LocalDate startDate,
                LocalDate endDate,
                @NotNull(message = "isWork는 null일 수 없습니다.") Boolean isWork) {

        public ForeignerCareers toEntity(UUID foreignerId) {
                return new ForeignerCareers(null, foreignerId, companyName, jobTitle, startDate, endDate, isWork);
        }
}
