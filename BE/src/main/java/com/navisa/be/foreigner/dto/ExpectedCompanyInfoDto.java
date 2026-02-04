package com.navisa.be.foreigner.dto;

import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "입사 예정 정보 dto")
public record ExpectedCompanyInfoDto(
        @Schema(description = "직무")
        String targetJob,
        @Schema(description = "회사명")
        String companyName,
        @Schema(description = "시작일")
        LocalDate startDate
) {

        public static ExpectedCompanyInfoDto of(ForeignerExpectedCompany expectedCompany) {
                return new ExpectedCompanyInfoDto(
                        expectedCompany.getJobTitle(),
                        expectedCompany.getCompanyName(),
                        expectedCompany.getStartDate()
                );
        }
}
