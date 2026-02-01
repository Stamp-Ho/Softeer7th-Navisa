package com.navisa.be.agent.dto.response;

import com.navisa.be.agent.model.entity.Badge;

public record BadgeResponse(

        Long badgeId,
        String badgeName
) {
    public static BadgeResponse from(Badge badge) {
        return new BadgeResponse(
                badge.getId(),
                badge.getBadgeName().name()
        );
    }
}
