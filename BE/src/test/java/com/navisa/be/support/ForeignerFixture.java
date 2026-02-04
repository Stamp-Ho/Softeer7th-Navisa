package com.navisa.be.support;

import com.navisa.be.foreigner.dto.ForeignerBasicInfoDto;
import com.navisa.be.foreigner.dto.*;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.FindForeignerDetailResponse;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ForeignerFixture {

    public static ForeignerRegisterRequest createForeignerRegisterRequest(List<Long> nationIds,
            List<Long> languageIds, boolean isWork) {
        return new ForeignerRegisterRequest(
                nationIds,
                languageIds,
                createEducationRequest(),
                List.of(createCareerRequest()),
                createExpectedCompanyRequest(),
                isWork);
    }

    public static ForeignerEducationDto createEducationRequest() {
        return new ForeignerEducationDto(
                "University",
                EducationDegreeLevel.BACHELOR,
                "Computer Science");
    }

    public static ForeignerCareerDto createCareerRequest() {
        return new ForeignerCareerDto(
                "Company A",
                "Developer",
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2022, 1, 1),
                true);
    }

    public static ForeignerExpectedCompanyDto createExpectedCompanyRequest() {
        return new ForeignerExpectedCompanyDto(
                "IT",
                "Software Engineer",
                LocalDate.now());
    }

    public static FindForeignerDetailResponse createFindForeignerDetailResponse() {
        return new FindForeignerDetailResponse(
                new ForeignerBasicInfoDto(
                        UUID.randomUUID(),
                        "고라니 099",
                        List.of(1L, 24L),
                        LocalDateTime.of(2026, 1, 28, 11, 27, 2),
                        false,
                        null
                ),
                new EducationInfoDto(
                        EducationDegreeLevel.ABOVE_MASTER,
                        "으악대학교",
                        "대박전공"
                ),
                List.of(12L, 100L, 1L, 4L),
                new CareerInfoDto(
                        13,
                        List.of(
                                new CareerInfoDto.CareerHistory("땡땡회사", "머시기 직무", "2023. 11. 02. ~ 2024. 11. 02.", 12),
                                new CareerInfoDto.CareerHistory("땡땡회사", "머시기 직무", "2023. 11. 02. ~ 현재", 1)
                        )
                ),
                new ExpectedCompanyInfoDto(
                        "웹 개발자",
                        "대박쩌는 IT회사",
                        LocalDate.of(2026, 1, 15)
                )
        );
    }
}
