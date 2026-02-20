package com.navisa.be.admin.controller;

import com.navisa.be.admin.dto.request.AgentPermitRequest;
import com.navisa.be.admin.service.AdminService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO :: 최종발표를 위해서 주석처리
//@Tag(
//        name = "Admin",
//        description = "개발자 내부용 API"
//)
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
public class AdminController {

    private final AdminService adminService;

//    @Operation(
//            summary = "새로운 행정사 승인 API",
//            description = "개발자가 새로운 행정사를 승인하기 위한 API입니다."
//    )
//    @HasUserType(UserType.ADMIN)
//    @PostMapping("/permit/agent")
//    public BaseResponse<Void> permitNewAgent(@Valid @RequestBody AgentPermitRequest request){
//        adminService.permitNewAgent(request);
//        return new BaseResponse<>(null);
//    }
}
