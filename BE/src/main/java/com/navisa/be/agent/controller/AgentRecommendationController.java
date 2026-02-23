package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.TopAgentByBadgeResponse;
import com.navisa.be.agent.service.AgentRecommendationService;
import com.navisa.be.agent.service.AgentSuggestionService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Agent Recommendation", description = "행정사 추천 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class AgentRecommendationController {

    private final AgentRecommendationService agentRecommendationService;
    private final AgentSuggestionService agentSuggestionService;

    @Operation(
            summary = "홈화면 외국인 맞춤 행정사 추천 API",
            description = "로그인한 외국인의 관심도와 행정사의 전문성을 계산하여 최적의 행정사 12명을 추천합니다"
    )
    @HasUserType(UserType.FILLED_FOREIGNER)
    @GetMapping("/user/agents")
    public BaseResponse<List<AgentCardResponse>> getPersonalizedAgents(
            @Parameter(hidden = true) @LoginUser String email) {
        List<AgentCardResponse> responses = agentRecommendationService.getPersonalizedAgents(email);
        return new BaseResponse<>(responses);
    }

    @Operation(
            summary = "홈화면 비로그인 사용자용 행정사 카드 랜덤 조회",
            description = "메인 페이지용 행정사 카드 12개를 랜덤으로 조회합니다. 로그인 시 전문분야가 노출됩니다"
    )
    @GetMapping("/guest/agents")
    public BaseResponse<List<AgentCardResponse>> getRandomAgentCards(@Parameter(hidden = true) @LoginUser(required = false) String email) {
        List<AgentCardResponse> agentCardResult = agentSuggestionService.getRandomAgentCards(email);
        return new BaseResponse<>(agentCardResult);
    }

    @Operation(
            summary = "홈화면 배지별 행정사 추천 목록 조회",
            description = "특정 배지를 보유한 행정사 중 리뷰 통계가 높은 상위 10명을 조회합니다"
    )
    @GetMapping("/badge")
    public BaseResponse<List<TopAgentByBadgeResponse>> getTopAgentsByBadge(@RequestParam(name = "badgeId") Long badgeId) {
        List<TopAgentByBadgeResponse> agentListResult = agentSuggestionService.getTop10AgentsByBadge(badgeId);
        return new BaseResponse<>(agentListResult);
    }
}
