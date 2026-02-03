package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentReview;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AgentReviewRepository extends JpaRepository<AgentReview, Long> {

    @Query("SELECT r FROM AgentReview r " +
            "WHERE r.feedbackContent IS NOT NULL AND r.feedbackContent <> '' " +
            "ORDER BY r.createdAt DESC")
    List<AgentReview> findTop4ValidFeedbacks(Pageable pageable);

    long countByAgentProfileId(UUID agentProfileId);
}
