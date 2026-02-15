package com.navisa.be.foreigner.controller;

import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.dto.request.ForeignerRegisterRequest;
import com.navisa.be.foreigner.service.ForeignerServiceFacade;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/foreigner")
@AllArgsConstructor
@Tag(name = "Foreigner Command", description = "외국인 프로필 등록/수정 API")
public class ForeignerCommandController {

    private final ForeignerServiceFacade foreignerServiceFacade;

    @Operation(summary = "외국인 프로필 등록/수정", description = "외국인 회원의 프로필 정보를 등록하거나 수정합니다. 모든 하위 정보(경력, 학력 등)를 포함하여 저장합니다.")
    @PostMapping("/profile")
    @HasUserType(UserType.UNFILLED_FOREIGNER)
    public BaseResponse<Void> registerForeignerProfile(
            @Parameter(hidden = true) @LoginUser String email,
            @Valid @RequestBody ForeignerRegisterRequest request) {

        foreignerServiceFacade.registerAllForeignerInfo(request, email);
        return new BaseResponse<>(ResponseStatus.CREATED_FOREIGNER_PROFILE, null);
    }
}
