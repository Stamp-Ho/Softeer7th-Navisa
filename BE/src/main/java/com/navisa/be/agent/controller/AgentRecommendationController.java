package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.service.AgentRecommendationService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Home - Agent", description = "홈 화면 행정사 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class AgentRecommendationController {

    private final AgentRecommendationService agentRecommendationService;
    private final ForeignerQueryService foreignerQueryService;

    @Operation(
            summary = "홈 화면 외국인 맞춤 행정사 추천 API",
            description = "로그인한 외국인의 관심도와 행정사의 전문성을 계산하여 최적의 행정사 12명을 추천합니다. "
    )
    @HasUserType(UserType.FILLED_FOREIGNER)
    @GetMapping("/user/agent")
    public BaseResponse<List<AgentCardResponse>> getPersonalizedAgents(
            @Parameter(hidden = true) @LoginUser String email) {

        UUID foreignerId = foreignerQueryService.getForeignerIdByEmail(email);

        List<AgentCardResponse> responses = agentRecommendationService.getPersonalizedAgents(foreignerId);

        return new BaseResponse<>(responses);
    }
}
