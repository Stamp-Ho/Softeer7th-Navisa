package com.navisa.be.foreigner.dto.response;

import com.navisa.be.foreigner.dto.ForeignerCareerDto;
import com.navisa.be.foreigner.dto.ForeignerEducationDto;
import com.navisa.be.foreigner.dto.ForeignerExpectedCompanyDto;

import java.util.List;

public record ForeignerQueryResponse(
        List<Long> nationIdList,
        List<Long> languageIdList,
        ForeignerEducationDto education,
        List<ForeignerCareerDto> foreignerCareers,
        ForeignerExpectedCompanyDto expectedCompany,
        boolean isIdle
) {
}
