package com.navisa.be.agent.dto.projection;

import java.util.UUID;

public record AgentSimpleProjection(
        UUID agentId,
        String name,
        String profileObjectKey,
        String officeAddress,
        Long specialityJobCount,
        Double activeScore) {
}
