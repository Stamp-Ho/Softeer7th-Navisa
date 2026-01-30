package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.AgentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgentProfileRepository extends JpaRepository<AgentProfile, UUID> {
}
