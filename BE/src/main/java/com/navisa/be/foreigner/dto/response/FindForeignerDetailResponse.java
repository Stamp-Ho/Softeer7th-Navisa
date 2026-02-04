package com.navisa.be.foreigner.dto.response;

import com.navisa.be.foreigner.dto.ForeignerBasicInfoDto;
import com.navisa.be.foreigner.dto.CareerInfoDto;
import com.navisa.be.foreigner.dto.EducationInfoDto;
import com.navisa.be.foreigner.dto.ExpectedCompanyInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "외국인 상세 조회 dto")
public record FindForeignerDetailResponse(
        ForeignerBasicInfoDto basicInfo,
        EducationInfoDto educationInfo,
        @Schema(description = "언어 id 리스트")
        List<Long> languageList,
        CareerInfoDto careerInfo,
        ExpectedCompanyInfoDto expectedCompanyInfo
) {
}
