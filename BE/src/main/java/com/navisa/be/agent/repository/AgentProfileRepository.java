package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.querydsl.AgentProfileQueryDsl;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentProfileRepository extends JpaRepository<AgentProfile, UUID>, AgentProfileQueryDsl {

    @Query(value = "SELECT ap.* FROM agent_profile ap " +
            "JOIN users u ON ap.user_id = u.user_id " +
            "WHERE u.user_type = 'VALID_AGENT' " +
            "ORDER BY RANDOM() LIMIT 12", nativeQuery = true)
    List<AgentProfile> findRandom12ValidAgents();

    @EntityGraph(attributePaths = { "specializedJobs" })
    Optional<AgentProfile> findWithSpecializedJobByUserId(UUID userId);

    Optional<AgentProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("SELECT p FROM AgentProfile p JOIN User u ON p.userId = u.id WHERE u.userType = 'VALID_AGENT'")
    List<AgentProfile> findAllValidAgentProfiles();

}
