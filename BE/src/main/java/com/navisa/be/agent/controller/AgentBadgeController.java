package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.response.BadgeResponse;
import com.navisa.be.agent.dto.response.HomeAgentBadgeResponse;
import com.navisa.be.global.web.response.BaseResponse;
import com.navisa.be.agent.service.AgentBadgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@AllArgsConstructor
@Tag(name = "Badge Query", description = "배지 정보 조회 API")
public class AgentBadgeController {

    private final AgentBadgeService badgeService;

    @Operation(summary = "전체 배지 목록 조회", description = "시스템에 등록된 모든 배지(15종)의 ID와 이름을 조회합니다. 리뷰 작성 시 배지 선택 리스트를 구성할 때 사용합니다.")
    @GetMapping("/badge-list")
    public BaseResponse<List<BadgeResponse>> getBadges() {
        List<BadgeResponse> badgeListResult = badgeService.getAllBadges();
        return new BaseResponse<>(badgeListResult);
    }

    @Operation(summary = "배지별 행정사 추천 목록 조회", description = "특정 배지를 보유한 행정사 중 리뷰 통계가 높은 상위 10명을 조회합니다.")
    @GetMapping("/badge")
    public BaseResponse<List<HomeAgentBadgeResponse>> getTopAgentsByBadge(@RequestParam(name = "badgeId") Long badgeId) {
        List<HomeAgentBadgeResponse> agentListResult = badgeService.getTop10AgentsByBadge(badgeId);
        return new BaseResponse<>(agentListResult);
    }
}
