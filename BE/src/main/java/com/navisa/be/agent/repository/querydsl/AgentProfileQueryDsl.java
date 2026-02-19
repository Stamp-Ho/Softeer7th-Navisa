package com.navisa.be.agent.repository.querydsl;

import com.navisa.be.agent.dto.projection.AgentSimpleProjection;
import com.navisa.be.agent.dto.AgentCardQueryDto;
import com.navisa.be.global.web.request.SliceRequest;

import java.util.List;
import java.util.UUID;

public interface AgentProfileQueryDsl {
    List<AgentSimpleProjection> findByFilters(AgentCardQueryDto dto, SliceRequest<UUID> slice);

    List<AgentSimpleProjection> findAllValidAgentProjections();
}
