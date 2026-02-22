package com.navisa.be.agent.repository;

import com.navisa.be.agent.dto.projection.AgentSpecializedJobWithScoreProjection;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AgentSpecializedJobRepository extends JpaRepository<AgentSpecializedJob, Long> {

    long countByAgentProfile(AgentProfile agentProfile);

    @Query("""
            SELECT asj.agentProfile.id AS agentId,
                   asj.jobCode.id      AS jobCodeId,
                   ajs.accumulatedReviewReliability AS reviewScore
            FROM AgentSpecializedJob asj
            LEFT JOIN AgentSpecializedJobSummary ajs
                ON asj.agentProfile.id = ajs.agentId
               AND asj.jobCode.id      = ajs.jobCode.id
            WHERE asj.agentProfile.id IN :agentIds
            """)
    List<AgentSpecializedJobWithScoreProjection> findAllWithReviewScoreByAgentIds(
            @Param("agentIds") List<UUID> agentIds);
}
