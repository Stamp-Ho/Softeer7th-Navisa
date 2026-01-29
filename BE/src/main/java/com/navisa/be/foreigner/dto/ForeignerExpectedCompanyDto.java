package com.navisa.be.foreigner.dto;

import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ForeignerExpectedCompanyDto(
                @NotBlank(message = "companyName은 빈 값일 수 없습니다.") String companyName,
                @NotBlank(message = "jobTitle은 빈 값일 수 없습니다.") String jobTitle,
                @NotNull(message = "startDate는 null일 수 없습니다.") LocalDate startDate) {

        public ForeignerExpectedCompany toEntity(UUID foreignerId) {
                return new ForeignerExpectedCompany(null, foreignerId, companyName, jobTitle, startDate);
        }
}
