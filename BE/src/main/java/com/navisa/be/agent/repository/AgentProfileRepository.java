package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AgentProfileRepository extends JpaRepository<AgentProfile, UUID> {

    @Query(value = "SELECT * FROM agent_profile ORDER BY RANDOM() LIMIT 12", nativeQuery = true)
    List<AgentProfile> findRandom12();
}
