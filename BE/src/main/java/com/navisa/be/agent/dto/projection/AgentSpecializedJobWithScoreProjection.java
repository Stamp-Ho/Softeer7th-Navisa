package com.navisa.be.agent.dto.projection;

import java.util.UUID;

public interface AgentSpecializedJobWithScoreProjection {
    UUID getAgentId();

    Long getJobCodeId();

    Double getReviewScore();
}
