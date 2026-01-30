package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.dto.request.RegisterAgentProfileRequest;
import com.navisa.be.agent.service.AgentProfileService;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agent Profile", description = "행정사 프로필 API")
@RequestMapping("/api/agent")
@RestController
public class AgentProfileController {

    private final AgentProfileService agentProfileService;

    public AgentProfileController(AgentProfileService agentProfileService) {
        this.agentProfileService = agentProfileService;
    }

    @Operation(
            summary = "행정사 프로필 등록 API",
            description = "노션 링크 참고 https://www.notion.so/bside/15443d2553684a4fb58c43a38c54a6af?source=copy_link"
    )
    @PostMapping("/profile")
    public BaseResponse<Void> registerAgentProfile(@Valid @RequestBody RegisterAgentProfileRequest request,
                                                   @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        RegisterAgentProfileCommand command = new RegisterAgentProfileCommand(request, loginUserEmail);
        agentProfileService.registerAgentProfile(command);
        return new BaseResponse(null);
    }
}
