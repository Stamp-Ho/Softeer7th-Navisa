package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.querydsl.AgentProfileRepositoryQueryDsl;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentProfileRepository extends JpaRepository<AgentProfile, UUID>, AgentProfileRepositoryQueryDsl {

    @Query(value = "SELECT * FROM agent_profile ORDER BY RANDOM() LIMIT 12", nativeQuery = true)
    List<AgentProfile> findRandom12();

    @EntityGraph(attributePaths = {"specializedJobs"})
    Optional<AgentProfile> findWithSpecializedJobByUserId(UUID userId);

    Optional<AgentProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("SELECT p FROM AgentProfile p JOIN User u ON p.userId = u.id WHERE u.userType = 'VALID_AGENT'")
    List<AgentProfile> findAllValidAgentProfiles();
}
