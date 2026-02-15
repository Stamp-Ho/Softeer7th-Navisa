package com.navisa.be.application.controller;

import com.navisa.be.application.dto.request.VisaApplicationFinishRequest;
import com.navisa.be.application.dto.request.VisaApplicationStatusUpdateRequest;
import com.navisa.be.application.dto.request.ProfilePhotoSaveRequest;
import com.navisa.be.application.dto.request.VisaApplicationSaveRequest;
import com.navisa.be.application.dto.response.VisaApplicationFinishResponse;
import com.navisa.be.application.dto.response.VisaApplicationSaveResponse;
import com.navisa.be.application.service.ApplicationCommandService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/application-forms")
@RequiredArgsConstructor
@Tag(name = "Visa Application Command", description = "비자 신청서 조작(저장/수정) API")
public class ApplicationCommandController {

    private final ApplicationCommandService applicationCommandService;

    @Operation(summary = "비자 신청서 자동 저장", description = "작성 중인 비자 신청서의 섹션 데이터를 저장하거나 수정합니다.")
    @PostMapping("/{formId}")
    public BaseResponse<VisaApplicationSaveResponse> saveVisaForm(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID") @PathVariable(name = "formId") UUID formId,
            @Valid @RequestBody VisaApplicationSaveRequest request) {

        VisaApplicationSaveResponse response = applicationCommandService.saveVisaForm(email, formId, request);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "비자 신청서 증명사진 저장", description = "S3에 업로드된 증명사진의 경로를 비자 신청서 테이블에 저장합니다.")
    @PostMapping("/{formId}/image")
    public BaseResponse<VisaApplicationSaveResponse> saveProfilePhoto(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID") @PathVariable(name = "formId") UUID formId,
            @Valid @RequestBody ProfilePhotoSaveRequest request) {

        VisaApplicationSaveResponse response = applicationCommandService.saveProfilePhoto(email, formId, request.profileObjectKey());
        return new BaseResponse<>(response);
    }

    @Operation(summary = "신청서 작성 상태 변경", description = "비자 신청서의 작성 완료(isDone) 상태를 true 또는 false로 변경합니다.")
    @PatchMapping("/{formId}/status")
    public BaseResponse<VisaApplicationSaveResponse> updateApplicationStatus(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID") @PathVariable(name = "formId") UUID formId,
            @Valid @RequestBody VisaApplicationStatusUpdateRequest request) {

        VisaApplicationSaveResponse response = applicationCommandService.updateApplicationStatus(email, formId, request.isDone());
        return new BaseResponse<>(response);
    }

    @Operation(summary = "수임 종료 및 비자 신청서 복사본 생성", description = "비자 신청 프로세스를 완전히 종료하고, 행정사 정보가 없는 비자 신청서 복사본을 생성합니다.")
    @PatchMapping("/{formId}/status/finished")
    public BaseResponse<VisaApplicationFinishResponse> finishApplication(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID") @PathVariable(name = "formId") UUID formId,
            @Valid @RequestBody VisaApplicationFinishRequest request) {

        VisaApplicationFinishResponse response = applicationCommandService.finishApplication(email, formId, request.isFinished());
        return new BaseResponse<>(response);
    }

    @Operation(summary = "외국인용 강제 수임 종료 및 갱신", description = "가장 최근 신청서를 대상으로, 행정사 독촉 메일 발송 3일 후 외국인이 직접 종료합니다.")
    @HasUserType(UserType.FILLED_FOREIGNER)
    @PatchMapping("/status/finished")
    public BaseResponse<VisaApplicationFinishResponse> finishByForeigner(
            @Parameter(hidden = true) @LoginUser String email) {

        VisaApplicationFinishResponse response = applicationCommandService.finishByForeigner(email);
        return new BaseResponse<>(response);
    }
}
