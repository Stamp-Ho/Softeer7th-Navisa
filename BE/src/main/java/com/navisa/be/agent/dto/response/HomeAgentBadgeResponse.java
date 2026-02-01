package com.navisa.be.agent.dto.response;

import java.util.List;
import java.util.UUID;

public record HomeAgentBadgeResponse(
        Long reviewId,
        String reviewerInitial, // 마스킹 적용한 이름
        String reviewContent,
        UUID agentId,
        String agentName,
        String agentProfileImgUrl,
        List<Long> badgeTop2
) {
}
