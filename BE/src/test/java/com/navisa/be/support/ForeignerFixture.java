package com.navisa.be.support;

import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerDetailResponse;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;

import java.time.LocalDate;
import java.time.ZonedDateTime;
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

    public static ForeignerRegisterRequest.ForeignerEducationRegistration createEducationRequest() {
        return new ForeignerRegisterRequest.ForeignerEducationRegistration(
                "University",
                EducationDegreeLevel.BACHELOR,
                "Computer Science");
    }

    public static ForeignerRegisterRequest.ForeignerCareerRegistration createCareerRequest() {
        return new ForeignerRegisterRequest.ForeignerCareerRegistration(
                "Company A",
                "Developer",
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2022, 1, 1),
                true);
    }

    public static ForeignerRegisterRequest.ForeignerExpectedCompanyRegistration createExpectedCompanyRequest() {
        return new ForeignerRegisterRequest.ForeignerExpectedCompanyRegistration(
                "IT",
                "Software Engineer",
                LocalDate.now());
    }

    public static ForeignerDetailResponse createFindForeignerDetailResponse() {
        return new ForeignerDetailResponse(
                new ForeignerDetailResponse.ForeignerBasicInfo(
                        UUID.randomUUID(),
                        "고라니 099",
                        List.of(1L, 24L),
                        ZonedDateTime.now(),
                        false,
                        null
                ),
                new ForeignerDetailResponse.EducationInfo(
                        EducationDegreeLevel.ABOVE_MASTER,
                        "으악대학교",
                        "대박전공"
                ),
                List.of(12L, 100L, 1L, 4L),
                new ForeignerDetailResponse.CareerInfo(
                        13,
                        List.of(
                                new ForeignerDetailResponse.CareerInfo.CareerHistory("땡땡회사", "머시기 직무", "2023. 11. 02. ~ 2024. 11. 02.", 12),
                                new ForeignerDetailResponse.CareerInfo.CareerHistory("땡땡회사", "머시기 직무", "2023. 11. 02. ~ 현재", 1)
                        )
                ),
                new ForeignerDetailResponse.ExpectedCompanyInfo(
                        "웹 개발자",
                        "대박쩌는 IT회사",
                        LocalDate.of(2026, 1, 15)
                )
        );
    }
}
