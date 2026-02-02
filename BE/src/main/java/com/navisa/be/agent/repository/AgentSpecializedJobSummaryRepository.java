package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.common.model.entity.JobCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
