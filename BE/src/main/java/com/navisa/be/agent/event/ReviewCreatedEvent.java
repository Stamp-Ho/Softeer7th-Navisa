package com.navisa.be.agent.event;

import java.util.List;
import java.util.UUID;

public record ReviewCreatedEvent(
        UUID agentId,
        List<Long> badgeIds
) {
}
