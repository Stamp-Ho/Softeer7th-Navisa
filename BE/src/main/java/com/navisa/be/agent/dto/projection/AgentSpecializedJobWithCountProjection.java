package com.navisa.be.agent.dto.projection;

import java.util.UUID;

public record AgentSpecializedJobWithCountProjection(
        UUID agentId,
        Long jobCodeId,
        Integer count
) {
}
