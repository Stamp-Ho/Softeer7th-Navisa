package com.navisa.be.support;

import com.navisa.be.foreigner.dto.ForeignerCareerDto;
import com.navisa.be.foreigner.dto.ForeignerEducationDto;
import com.navisa.be.foreigner.dto.ForeignerExpectedCompanyDto;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;

import java.time.LocalDate;
import java.util.List;

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
}
