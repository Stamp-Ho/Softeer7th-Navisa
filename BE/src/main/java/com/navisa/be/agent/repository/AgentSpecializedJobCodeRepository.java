package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.AgentProfile;
import com.navisa.be.agent.model.AgentSpecializedJobCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentSpecializedJobCodeRepository extends JpaRepository<AgentSpecializedJobCode, Long> {

    long countByAgentProfile(AgentProfile agentProfile);
}
