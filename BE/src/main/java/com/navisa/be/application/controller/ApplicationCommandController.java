package com.navisa.be.application.controller;

import com.navisa.be.application.dto.request.ApplicationStatusUpdateRequest;
import com.navisa.be.application.dto.request.ProfilePhotoSaveRequest;
import com.navisa.be.application.dto.request.VisaApplicationSaveRequest;
import com.navisa.be.application.dto.response.VisaApplicationSaveResponse;
import com.navisa.be.application.service.ApplicationCommandService;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/application-forms")
@RequiredArgsConstructor
@Tag(name = "Visa Application Command", description = "비자 신청서 조작(저장/수정) API")
public class ApplicationCommandController {

    private final ApplicationCommandService applicationCommandService;

    @Operation(summary = "비자 신청서 자동 저장", description = "작성 중인 비자 신청서의 섹션 데이터를 저장하거나 수정합니다.")
    @PostMapping()
    public BaseResponse<VisaApplicationSaveResponse> saveVisaForm(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID (visaFormId)")
            @RequestParam(name = "id") UUID id,
            @Valid @RequestBody VisaApplicationSaveRequest request) {

        VisaApplicationSaveResponse response = applicationCommandService.saveVisaForm(email, id, request);
        return new BaseResponse<>(response);
    }

    @Operation(summary = "비자 신청서 증명사진 저장", description = "S3에 업로드된 증명사진의 경로를 외국인 프로필에 저장합니다.")
    @PostMapping("/image")
    public BaseResponse<VisaApplicationSaveResponse> saveProfilePhoto(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID (visaFormId)")
            @RequestParam(name = "id") UUID id,
            @Valid @RequestBody ProfilePhotoSaveRequest request) {

        VisaApplicationSaveResponse response = applicationCommandService.saveProfilePhoto(email, id, request.profileObjectKey());
        return new BaseResponse<>(response);
    }

    @Operation(summary = "신청서 작성 상태 변경", description = "비자 신청서의 작성 완료(isDone) 상태를 true 또는 false로 변경합니다.")
    @PatchMapping("/status")
    public BaseResponse<VisaApplicationSaveResponse> updateApplicationStatus(
            @Parameter(hidden = true) @LoginUser String email,
            @Parameter(description = "비자 문서 ID (visaFormId)")
            @RequestParam(name = "id") UUID id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request) {

        VisaApplicationSaveResponse response = applicationCommandService.updateApplicationStatus(email, id, request.isDone());
        return new BaseResponse<>(response);
    }
}
