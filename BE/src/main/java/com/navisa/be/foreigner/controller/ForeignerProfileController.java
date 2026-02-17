package com.navisa.be.foreigner.controller;

import com.navisa.be.foreigner.dto.request.ForeignerDetailRequest;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.dto.response.ForeignerDetailResponse;
import com.navisa.be.foreigner.dto.response.ForeignerProgressResponse;
import com.navisa.be.foreigner.dto.response.ForeignerQueryResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.foreigner.service.ForeignerProfileDetailService;
import com.navisa.be.foreigner.service.ForeignerRegistrationService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/foreigner")
@AllArgsConstructor
@Tag(name = "Foreigner Profile", description = "외국인 프로필 관리 및 상세 조회 API")
public class ForeignerProfileController {

    private final ForeignerRegistrationService foreignerRegistrationService;
    private final ForeignerProfileDetailService foreignerProfileDetailService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;

    @Operation(summary = "외국인 프로필 등록/수정 API",
            description = "외국인 회원의 프로필 정보를 등록하거나 수정합니다. 모든 하위 정보(경력, 학력 등)를 포함하여 저장합니다.")
    @PostMapping("/profile")
    @HasUserType({UserType.UNFILLED_FOREIGNER, UserType.FILLED_FOREIGNER})
    public BaseResponse<Void> registerForeignerProfile(
            @Parameter(hidden = true) @LoginUser String email,
            @Valid @RequestBody ForeignerRegisterRequest request) {

        foreignerRegistrationService.registerAllForeignerInfo(request, email);
        return new BaseResponse<>(ResponseStatus.CREATED_FOREIGNER_PROFILE, null);
    }

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 외국인 회원의 전체 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public BaseResponse<ForeignerQueryResponse> findForeignerProfile(
            @Parameter(hidden = true) @LoginUser String email) {
        UUID foreignerId = foreignerProfileCrudService.getForeignerIdByEmail(email);
        ForeignerQueryResponse response = foreignerProfileDetailService.findForeignerTotalInfo(foreignerId);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "외국인 상세 요건 입력 여부 확인", description = "현재 로그인한 외국인 회원의 필수 상세 요건 입력 상태를 조회합니다.")
    @GetMapping("/requirements")
    public BaseResponse<ForeignerStatusResponse> checkForeignerFilledStatus(
            @Parameter(hidden = true) @LoginUser String email) {
        ForeignerStatusResponse response = foreignerProfileDetailService.checkForeignerFilledStatus(email);
        return new BaseResponse<>(response);
    }

    @Operation(
            summary = "행정사의 외국인 상세 조회",
            description = "승인된 행정사 회원이 외국인을 상세 조회할 때 사용되는 API입니다."
    )
    @GetMapping("/{foreignerId}")
    @HasUserType({UserType.VALID_AGENT})
    public BaseResponse<ForeignerDetailResponse> findForeignerDetail(@PathVariable UUID foreignerId,
                                                                     @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        ForeignerDetailRequest request = new ForeignerDetailRequest(loginUserEmail, foreignerId);
        ForeignerDetailResponse response = foreignerProfileDetailService.findForeignerDetail(request);
        return new BaseResponse<>(response);
    }

    @Operation(
            summary = "외국인 진행 상태 조회",
            description = "현재 로그인한 외국인의 매칭, 리뷰 작성, 피드백 작성 및 수임 완료 상태를 리스트로 조회합니다."
    )
    @HasUserType({UserType.FILLED_FOREIGNER})
    @GetMapping("/progress")
    public BaseResponse<ForeignerProgressResponse> getForeignerProgress(
            @Parameter(hidden = true) @LoginUser String email) {
        return new BaseResponse<>(foreignerProfileDetailService.getForeignerProgress(email));
    }
}
