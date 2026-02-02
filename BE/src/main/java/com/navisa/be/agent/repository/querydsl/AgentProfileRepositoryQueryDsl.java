package com.navisa.be.agent.repository.querydsl;

import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.common.dto.request.SliceRequest;

import java.util.List;
import java.util.UUID;

public interface AgentProfileRepositoryQueryDsl {
    List<AgentProfile> findByFilters(AgentCardRequest request, SliceRequest<UUID> slice);
}
