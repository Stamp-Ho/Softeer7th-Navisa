package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJobCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentSpecializedJobCodeRepository extends JpaRepository<AgentSpecializedJobCode, Long> {

    long countByAgentProfile(AgentProfile agentProfile);
}
