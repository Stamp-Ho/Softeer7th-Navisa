package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.FeedbackResponse;
import com.navisa.be.agent.service.AgentHomeService;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Agent Home", description = "홈 화면 내 행정사 관련 API")
@RequestMapping("/api/home")
@RestController
@RequiredArgsConstructor
public class AgentHomeController {

    private final AgentHomeService agentHomeService;

    @Operation(summary = "행정사 후기 최신순 조회", description = "최신순으로 등록된 후기 4개를 조회합니다.")
    @GetMapping("/feedback")
    public BaseResponse<List<FeedbackResponse>> getLatestAgentReviews() {
        List<FeedbackResponse> feedbackResponses = agentHomeService.getLatestFeedbacks();
        return new BaseResponse<>(feedbackResponses);
    }

    @Operation(summary = "행정사 카드 랜덤 조회", description = "메인 페이지용 행정사 카드 12개를 랜덤으로 조회합니다. 로그인 시 전문분야가 노출됩니다.")
    @GetMapping("/guest/agents")
    public BaseResponse<List<AgentCardResponse>> getRandomAgentCards(
            @Parameter(hidden = true) @LoginUser(required = false) String email) {
        List<AgentCardResponse> agentCardResult = agentHomeService.getRandomAgentCards(email);
        return new BaseResponse<>(agentCardResult);
    }
}
