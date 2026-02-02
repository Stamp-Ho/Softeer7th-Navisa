package com.navisa.be.agent.event;

import java.util.List;
import java.util.UUID;

public record ReviewCreatedSpecializedJobEvent(
        UUID agentId,
        List<Long> specializedJobIds
) {
}
