package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentLanguageRepository extends JpaRepository<AgentLanguage, Long> {

    long countByAgentProfile(AgentProfile agentProfile);
}
