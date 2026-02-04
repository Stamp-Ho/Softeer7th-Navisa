package com.navisa.be.foreigner.dto;

import com.navisa.be.foreigner.model.entity.ForeignerCareers;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "경력 dto")
public record CareerInfoDto(
        @Schema(description = "총 경력 개월수")
        Integer totalCareerMonths,
        List<CareerHistory> history
) {

    public static CareerInfoDto of(List<ForeignerCareers> careers) {
        // 외국인의 총 경력 개월 수 계산
        // 아래 연산은 리스트가 비어 있으면 0을 반환한다
        int totalCareerMonths = careers.stream()
                .mapToInt(ForeignerCareers::getDurationMonths)
                .sum();

        List<CareerInfoDto.CareerHistory> historys = careers.stream()
                .map(career -> {
                    return new CareerInfoDto.CareerHistory(
                            career.getCompanyName(),
                            career.getJobTitle(),
                            career.getFormattedPeriod(),
                            career.getDurationMonths()
                    );
                })
                .toList();

        return new CareerInfoDto(totalCareerMonths, historys);
    }

    @Schema(description = "경력 히스토리")
    public record CareerHistory(
            @Schema(description = "회사명")
            String companyName,
            @Schema(description = "직무명")
            String jobTitle,
            @Schema(description = "기간")
            String period,
            @Schema(description = "개월수")
            Integer durationMonths
    ) {}
}
