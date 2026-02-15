package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.global.common.model.entity.JobCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentSpecializedJobSummaryRepository extends JpaRepository<AgentSpecializedJobSummary, Long> {

    @Query("SELECT s FROM AgentSpecializedJobSummary s " +
            "JOIN FETCH s.jobCode " +
            "WHERE s.agentId = :agentId " +
            "ORDER BY s.count DESC, s.jobCode.id ASC "
    )
    List<AgentSpecializedJobSummary> findTop2SummaryByAgentId(UUID agentId, Pageable pageable);

    Optional<AgentSpecializedJobSummary> findByAgentIdAndJobCode(UUID uuid, JobCode jobCode);

    @Modifying
    @Query(value = """
    INSERT INTO agent_specialized_job_summary 
        (agent_id, job_code_id, accumulated_review_reliability, count, created_at, updated_at) 
    VALUES (:agentId, :jobCodeId, :reviewWeight, 1, NOW(), NOW())
    ON CONFLICT (agent_id, job_code_id) 
    DO UPDATE SET 
        accumulated_review_reliability = agent_specialized_job_summary.accumulated_review_reliability + :reviewWeight,
        count = agent_specialized_job_summary.count + 1,
        updated_at = NOW()
    """, nativeQuery = true)
    void upsertReliability(@Param("agentId") UUID agentId,
                           @Param("jobCodeId") Long jobCodeId,
                           @Param("reviewWeight") double reviewWeight);

    @Query("SELECT j FROM JobCode j WHERE j.id = :jobCodeId")
    JobCode getReferenceJobCode(Long jobCodeId);

    @Query("SELECT s FROM AgentSpecializedJobSummary s JOIN FETCH s.jobCode WHERE s.agentId IN :agentIds")
    List<AgentSpecializedJobSummary> findAllByAgentIdIn(@Param("agentIds") List<UUID> agentIds);
}
