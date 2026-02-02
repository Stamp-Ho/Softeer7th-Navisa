package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentSpecializedJobRepository extends JpaRepository<AgentSpecializedJob, Long> {

    long countByAgentProfile(AgentProfile agentProfile);
}
