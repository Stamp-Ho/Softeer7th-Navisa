package com.navisa.be.application.controller;

import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.dto.response.VisaApplicationCardResponse;
import com.navisa.be.application.dto.response.VisaApplicationDetailResponse;
import com.navisa.be.application.service.ApplicationQueryService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.annotation.SliceInfo;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/application-forms")
@RequiredArgsConstructor
@Tag(name = "Visa Application Query", description = "비자 신청서 조회 API")
public class ApplicationQueryController {

    private final ApplicationQueryService applicationQueryService;

    @Operation(summary = "최근 수정한 비자 신청서 리스트 조회", description = "로그인한 행정사가 담당하는 서류 중 최근 수정된 6개를 조회합니다.")
    @GetMapping("/recent-applications")
    public BaseResponse<List<RecentVisaFormsResponse>> getRecentVisaForms(
            @Parameter(hidden = true) @LoginUser String email) {

        List<RecentVisaFormsResponse> response = applicationQueryService.getRecentVisaForms(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "외국인용 최신 비자 신청서 단건 조회", description = "로그인한 외국인의 신청서 중 가장 최근에 생성된 서류를 상세 조회합니다.")
    @GetMapping("/foreigner")
    @HasUserType(UserType.FILLED_FOREIGNER)
    public BaseResponse<VisaApplicationDetailResponse> getLatestVisaFormForForeigner(
            @Parameter(hidden = true) @LoginUser String email) {

        VisaApplicationDetailResponse response = applicationQueryService.getLatestVisaFormForForeigner(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "행정사용 비자 신청서 단건 상세 조회", description = "행정사가 특정 ID를 가진 비자 신청서의 상세 내용을 조회합니다.")
    @GetMapping("/agent/{formId}")
    @HasUserType(UserType.VALID_AGENT)
    public BaseResponse<VisaApplicationDetailResponse> getVisaFormForAgent(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "조회할 신청서 ID") @PathVariable UUID formId) {

        VisaApplicationDetailResponse response = applicationQueryService.getVisaFormForAgent(email, formId);
        return new BaseResponse<>(response);
    }

    @Operation(
            summary = "행정사용 비자 신청서 전체 조회(필터 가능)",
            description = "행정사가 수임했던 비자 신청서들을 조회합니다.",
            parameters = {
                    @Parameter(name = "lastElementId", description = "마지막으로 조회한 신청서 ID (첫 페이지는 생략)", schema = @Schema(type = "string", format = "uuid")),
                    @Parameter(name = "size", description = "페이지 크기 (기본값: 18, 최대: 18)", schema = @Schema(type = "integer"))
            }
    )
    @GetMapping()
    @HasUserType(UserType.VALID_AGENT)
    public BaseResponse<SliceResponse<VisaApplicationCardResponse, UUID>> getVisaFormsForAgent(
            @Parameter(hidden = true) @LoginUser String email,
            @ParameterObject @SliceInfo(size = 18, max = 18) SliceRequest<UUID> slice,
            @RequestParam(required = false) Boolean complete) {

        return new BaseResponse<>(applicationQueryService.findVisaFormsByFilter(email, slice, complete));
    }
}
