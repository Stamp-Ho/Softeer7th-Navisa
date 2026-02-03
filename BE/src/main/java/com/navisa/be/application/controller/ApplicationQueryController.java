package com.navisa.be.application.controller;

import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.service.ApplicationQueryService;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/visa-forms")
@RequiredArgsConstructor
@Tag(name = "Visa Application Query", description = "비자 신청서 조회 API")
public class ApplicationQueryController {

    private final ApplicationQueryService visaApplicationService;

    @Operation(summary = "최근 수정한 비자 신청서 리스트 조회", description = "로그인한 행정사가 담당하는 서류 중 최근 수정된 6개를 조회합니다.")
    @GetMapping("/recent-applications")
    public BaseResponse<List<RecentVisaFormsResponse>> getRecentVisaForms(@LoginUser String email) {
        List<RecentVisaFormsResponse> response = visaApplicationService.getRecentVisaForms(email);
        return new BaseResponse<>(response);
    }
}
