package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.request.CreateAgentFeedbackRequest;
import com.navisa.be.agent.dto.request.CreateAgentReviewRequest;
import com.navisa.be.agent.dto.response.BadgeResponse;
import com.navisa.be.agent.dto.response.FeedbackResponse;
import com.navisa.be.agent.service.AgentReviewService;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.agent.service.AgentBadgeService;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Agent Review", description = "행정사 뱃지 리뷰와 후기 관련 API")
public class AgentReviewController {

    private final AgentBadgeService agentBadgeService;
    private final AgentReviewService agentReviewService;

    @Operation(
            summary = "전체 배지 목록 조회",
            description = "시스템에 등록된 모든 배지(15종)의 ID와 이름을 조회합니다. 리뷰 작성 시 배지 선택 리스트를 구성할 때 사용합니다"
    )
    @GetMapping("/api/home/badge-list")
    public BaseResponse<List<BadgeResponse>> getBadges() {
        List<BadgeResponse> badgeListResult = agentBadgeService.getAllBadges();
        return new BaseResponse<>(badgeListResult);
    }

    @Operation(
            summary = "행정사 후기 최신순 조회",
            description = "최신순으로 등록된 후기 3개를 조회합니다"
    )
    @GetMapping("/api/home/feedback")
    public BaseResponse<List<FeedbackResponse>> getLatestAgentReviews() {
        List<FeedbackResponse> feedbackResponses = agentReviewService.getLatestFeedbacks();
        return new BaseResponse<>(feedbackResponses);
    }

    @Operation(
            summary = "행정사 리뷰 작성 API",
            description = "외국인이 특정 행정사에게 리뷰를 작성할 때 사용하는 API입니다"
    )
    @HasUserType(UserType.FILLED_FOREIGNER)
    @PostMapping("/api/agent/reviews")
    public BaseResponse<Void> createAgentReview(@Valid @RequestBody CreateAgentReviewRequest request,
                                                @Parameter(hidden = true) @LoginUser String loginUserEmail){
        agentReviewService.registerAgentReview(loginUserEmail, request);
        return new BaseResponse<>(null);
    }

    @Operation(
            summary = "행정사 피드백 등록 API",
            description = "외국인이 리뷰 작성 후, 해당 계약 건에 대해 구체적인 피드백 내용을 등록할 때 사용하는 API입니다"
    )
    @HasUserType(UserType.FILLED_FOREIGNER)
    @PostMapping("/api/agent/feedback")
    public BaseResponse<Void> createAgentFeedback(@Valid @RequestBody CreateAgentFeedbackRequest request,
                                                  @Parameter(hidden = true) @LoginUser String loginUserEmail) {
        agentReviewService.createAgentFeedback(loginUserEmail, request.content());
        return new BaseResponse<>(null);
    }
}
