package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentBadge;
import com.navisa.be.agent.model.entity.AgentReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgentBadgeRepository extends JpaRepository<AgentBadge, Long> {

    @Query("SELECT r FROM AgentReview r " +
            "JOIN AgentBadge ab ON ab.agentReview.id = r.id " +
            "WHERE ab.badge.id = :badgeId " +
            "ORDER BY r.createdAt DESC")
    List<AgentReview> findByBadgeId(@Param("badgeId") Long badgeId, Pageable pageable);
}
