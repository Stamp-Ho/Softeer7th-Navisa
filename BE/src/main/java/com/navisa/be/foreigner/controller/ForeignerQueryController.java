package com.navisa.be.foreigner.controller;

import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.foreigner.service.ForeignerServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/foreigner")
@AllArgsConstructor
@Tag(name = "Foreigner Query", description = "외국인 프로필 조회 API")
public class ForeignerQueryController {

    private final ForeignerServiceFacade foreignerServiceFacade;
    private final ForeignerQueryService foreignerQueryService;

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 외국인 회원의 전체 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public BaseResponse<ForeignerQueryResponse> findForeignerProfile(@LoginUser String email) {
        ForeignerQueryResponse response = foreignerServiceFacade.findForeignerTotalInfo(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "외국인 상세 요건 입력 여부 확인", description = "현재 로그인한 외국인 회원의 필수 상세 요건 입력 상태를 조회합니다.")
    @GetMapping("/requirements")
    public BaseResponse<ForeignerStatusResponse> checkForeignerFilledStatus(@LoginUser String email) {
        ForeignerStatusResponse response = foreignerQueryService.checkForeignerFilledStatus(email);
        return new BaseResponse<>(response);
    }
}
