package com.navisa.be.agent.dto.response;

import com.navisa.be.agent.model.entity.AgentProfile;

import java.util.List;
import java.util.UUID;

public record AgentCardResponse(
        UUID agentId,
        String agentName,
        String profileImgUrl,
        String officeAddress,
        List<Long> agentSpecialityTop2,
        List<Long> badgeTop2
) {
    public static AgentCardResponse of(AgentProfile agent, List<Long> specialities, List<Long> badges) {
        return new AgentCardResponse(
                agent.getId(),
                agent.getName(),
                agent.getProfileImageUrl(),
                agent.getOfficeAddress(),
                specialities, // 로그인 시 List, 비로그인 시 null
                badges
        );
    }
}