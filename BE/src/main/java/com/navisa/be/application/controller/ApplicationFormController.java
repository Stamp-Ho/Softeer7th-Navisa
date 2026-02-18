package com.navisa.be.application.controller;

import com.navisa.be.application.dto.request.ApplicationFormFinishedStatusRequest;
import com.navisa.be.application.dto.request.ApplicationFormDoneStatusRequest;
import com.navisa.be.application.dto.request.ForeignerProfilePhotoKeyRequest;
import com.navisa.be.application.dto.request.ApplicationFormSectionDataRequest;
import com.navisa.be.application.dto.response.*;
import com.navisa.be.application.service.ApplicationFormForAgentService;
import com.navisa.be.application.service.ApplicationFormForForeignerService;
import com.navisa.be.application.service.ApplicationFormRegistrationService;
import com.navisa.be.application.service.ApplicationFormSearchService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.annotation.SliceInfo;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/application-forms")
@RequiredArgsConstructor
@Tag(name = "Visa Application Form API", description = "비자 신청서 관련 API")
public class ApplicationFormController {

    private final ApplicationFormForForeignerService applicationFormForForeignerService;
    private final ApplicationFormForAgentService applicationFormForAgentService;
    private final ApplicationFormSearchService applicationFormSearchService;
    private final ApplicationFormRegistrationService applicationFormRegistrationService;

    @Operation(summary = "비자 신청서 자동 저장", description = "작성 중인 비자 신청서의 섹션 데이터를 저장하거나 수정합니다.")
    @PostMapping("/{formId}")
    @HasUserType({ UserType.FILLED_FOREIGNER, UserType.VALID_AGENT })
    public BaseResponse<ApplicationFormIdResponse> saveVisaForm(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID") @PathVariable UUID formId,
            @Valid @RequestBody ApplicationFormSectionDataRequest request) {

        ApplicationFormIdResponse response = applicationFormRegistrationService.saveApplicationForm(email, formId, request);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "비자 신청서 증명사진 저장", description = "S3에 업로드된 증명사진의 경로를 비자 신청서 테이블에 저장합니다.")
    @PostMapping("/{formId}/image")
    @HasUserType({ UserType.FILLED_FOREIGNER, UserType.VALID_AGENT })
    public BaseResponse<ApplicationFormIdResponse> saveProfilePhoto(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID") @PathVariable UUID formId,
            @Valid @RequestBody ForeignerProfilePhotoKeyRequest request) {

        ApplicationFormIdResponse response = applicationFormRegistrationService.saveProfilePhoto(email, formId, request.profileObjectKey());
        return new BaseResponse<>(response);
    }

    @Operation(summary = "신청서 작성 상태 변경", description = "비자 신청서의 작성 완료(isDone) 상태를 true 또는 false로 변경합니다.")
    @PatchMapping("/{formId}/status")
    @HasUserType(UserType.VALID_AGENT)
    public BaseResponse<ApplicationFormIdResponse> updateApplicationStatus(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID") @PathVariable UUID formId,
            @Valid @RequestBody ApplicationFormDoneStatusRequest request) {

        ApplicationFormIdResponse response = applicationFormForAgentService.updateApplicationStatus(email, formId, request.isDone());
        return new BaseResponse<>(response);
    }

    @Operation(summary = "수임 종료 및 비자 신청서 복사본 생성", description = "비자 신청 프로세스를 완전히 종료하고, 행정사 정보가 없는 비자 신청서 복사본을 생성합니다.")
    @PatchMapping("/{formId}/status/finished")
    @HasUserType(UserType.VALID_AGENT)
    public BaseResponse<ApplicationFormFinishedStatusResponse> finishApplication(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID") @PathVariable UUID formId,
            @Valid @RequestBody ApplicationFormFinishedStatusRequest request) {

        ApplicationFormFinishedStatusResponse response = applicationFormForAgentService.finishApplication(email, formId, request.isFinished());
        return new BaseResponse<>(response);
    }

    @Operation(summary = "외국인용 강제 수임 종료 및 갱신", description = "가장 최근 신청서를 대상으로, 행정사 독촉 메일 발송 3일 후 외국인이 직접 종료합니다.")
    @PatchMapping("/status/finished")
    @HasUserType(UserType.FILLED_FOREIGNER)
    public BaseResponse<ApplicationFormFinishedStatusResponse> finishByForeigner(
            @Parameter(hidden = true) @LoginUser String email) {

        ApplicationFormFinishedStatusResponse response = applicationFormForForeignerService.finishByForeigner(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "최근 수정한 비자 신청서 리스트 조회", description = "로그인한 행정사가 담당하는 서류 중 최근 수정된 6개를 조회합니다.")
    @GetMapping("/recent-applications")
    @HasUserType(UserType.VALID_AGENT)
    public BaseResponse<List<RecentApplicationFormsResponse>> getRecentVisaForms(
            @Parameter(hidden = true) @LoginUser String email) {

        List<RecentApplicationFormsResponse> response = applicationFormSearchService.getRecentApplicationForms(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "외국인용 최신 비자 신청서 단건 조회", description = "로그인한 외국인의 신청서 중 가장 최근에 생성된 서류를 상세 조회합니다.")
    @GetMapping("/foreigner")
    @HasUserType(UserType.FILLED_FOREIGNER)
    public BaseResponse<ApplicationFormDetailResponse> getLatestVisaFormForForeigner(
            @Parameter(hidden = true) @LoginUser String email) {

        ApplicationFormDetailResponse response = applicationFormSearchService.getLatestApplicationFormForForeigner(email);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "행정사용 비자 신청서 단건 상세 조회", description = "행정사가 특정 ID를 가진 비자 신청서의 상세 내용을 조회합니다.")
    @GetMapping("/agent/{formId}")
    @HasUserType(UserType.VALID_AGENT)
    public BaseResponse<ApplicationFormDetailResponse> getVisaFormForAgent(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "조회할 신청서 ID") @PathVariable UUID formId) {

        ApplicationFormDetailResponse response = applicationFormSearchService.getApplicationFormForAgent(email, formId);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "행정사용 비자 신청서 전체 조회(필터 가능)", description = "행정사가 수임했던 비자 신청서들을 조회합니다.")
    @GetMapping
    @HasUserType(UserType.VALID_AGENT)
    public BaseResponse<SliceResponse<ApplicationFormCardResponse, UUID>> getVisaFormsForAgent(
            @Parameter(hidden = true) @LoginUser String email,
            @SliceInfo(size = 18, max = 18) SliceRequest<UUID> slice,
            @RequestParam(required = false) Boolean complete) {

        return new BaseResponse<>(applicationFormSearchService.findApplicationFormsByFilter(email, slice, complete));
    }
}
