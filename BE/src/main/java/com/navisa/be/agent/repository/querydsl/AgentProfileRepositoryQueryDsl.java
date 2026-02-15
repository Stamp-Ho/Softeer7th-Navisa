package com.navisa.be.agent.repository.querydsl;

import com.navisa.be.agent.dto.AgentCardQueryDto;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.global.web.request.SliceRequest;

import java.util.List;
import java.util.UUID;

public interface AgentProfileRepositoryQueryDsl {
    List<AgentProfile> findByFilters(AgentCardQueryDto dto, SliceRequest<UUID> slice);
}
