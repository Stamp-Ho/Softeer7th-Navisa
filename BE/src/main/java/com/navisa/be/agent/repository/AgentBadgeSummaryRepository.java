package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.Badge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentBadgeSummaryRepository extends JpaRepository<AgentBadgeSummary, Long> {

    Optional<AgentBadgeSummary> findByAgentIdAndBadge(UUID agentId, Badge badge);

    // 특정 행정사의 배지 중 획득 횟수가 가장 많은 순으로 조회
    @Query("SELECT s FROM AgentBadgeSummary s " +
            "JOIN FETCH s.badge " +
            "WHERE s.agentId = :agentId " +
            "ORDER BY s.count DESC, s.badge.id ASC ")
    List<AgentBadgeSummary> findTopKBadgeSummarysByAgentId(UUID agentId, Pageable pageable);

    @EntityGraph(attributePaths = "badge")
    List<AgentBadgeSummary> findAllByAgentIdIn(List<UUID> agentIds);
}
