package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.dto.request.RegisterAgentProfileRequest;
import com.navisa.be.agent.dto.response.GetJobCodeListResponse;
import com.navisa.be.agent.service.AgentProfileService;
import com.navisa.be.agent.service.JobCodeService;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Agent Profile", description = "행정사 프로필 API")
@RequiredArgsConstructor
@RequestMapping("/api/agent")
@RestController
public class AgentProfileController {

    private final AgentProfileService agentProfileService;
    private final JobCodeService jobCodeService;

    @Operation(
            summary = "행정사 프로필 등록 API",
            description = "행정사가 프로필을 등록하기 위해서 사용하는 API입니다. 추가적인 정보는 https://www.notion.so/bside/15443d2553684a4fb58c43a38c54a6af?source=copy_link를 참고해주세요"
    )
    @PostMapping("/profile")
    public BaseResponse<Void> registerAgentProfile(@Valid @RequestBody RegisterAgentProfileRequest request,
                                                   @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        RegisterAgentProfileCommand command = new RegisterAgentProfileCommand(request, loginUserEmail);
        agentProfileService.registerAgentProfile(command);
        return new BaseResponse(null);
    }

    @Operation(
            summary = "행정사 프로필 등록 중 직무코드 리스트 조회 API",
            description = "행정사가 프로필 등록 과정에서 직무코드 목록을 조회할 때 사용하는 API입니다. 추가적인 정보는 https://www.notion.so/bside/2fa22020273580268a2ec408b30182af?source=copy_link를 참고해주세요"
    )
    @GetMapping("/register-form/jobcodes")
    public BaseResponse<GetJobCodeListResponse> getJobCodeList(){
        GetJobCodeListResponse response = jobCodeService.getJobCodeList();
        return new BaseResponse<>(response);
    }
}
