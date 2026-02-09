package com.navisa.be.application.controller;

import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.dto.response.VisaApplicationDetailResponse;
import com.navisa.be.application.service.ApplicationQueryService;
import com.navisa.be.common.annotation.HasUserType;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/application-forms")
@RequiredArgsConstructor
@Tag(name = "Visa Application Query", description = "비자 신청서 조회 API")
public class ApplicationQueryController {

    private final ApplicationQueryService visaApplicationService;

    @Operation(summary = "최근 수정한 비자 신청서 리스트 조회", description = "로그인한 행정사가 담당하는 서류 중 최근 수정된 6개를 조회합니다.")
    @GetMapping("/recent-applications")
    public BaseResponse<List<RecentVisaFormsResponse>> getRecentVisaForms(
            @Parameter(hidden = true) @LoginUser String email) {

        List<RecentVisaFormsResponse> response = visaApplicationService.getRecentVisaForms(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "외국인용 최신 비자 신청서 단건 조회", description = "로그인한 외국인의 신청서 중 가장 최근에 생성된 서류를 상세 조회합니다.")
    @GetMapping("/foreigner")
    @HasUserType(UserType.FILLED_FOREIGNER)
    public BaseResponse<VisaApplicationDetailResponse> getLatestVisaFormForForeigner(
            @Parameter(hidden = true) @LoginUser String email) {

        VisaApplicationDetailResponse response = visaApplicationService.getLatestVisaFormForForeigner(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "행정사용 비자 신청서 단건 상세 조회", description = "행정사가 특정 ID를 가진 비자 신청서의 상세 내용을 조회합니다.")
    @GetMapping("/agent")
    @HasUserType(UserType.VALID_AGENT)
    public BaseResponse<VisaApplicationDetailResponse> getVisaFormForAgent(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "조회할 신청서 ID") @RequestParam(name = "id") UUID id) {

        VisaApplicationDetailResponse response = visaApplicationService.getVisaFormForAgent(email, id);
        return new BaseResponse<>(response);
    }
}
