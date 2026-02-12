package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ProposalStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentReviewRepository extends JpaRepository<AgentReview, Long> {

    @Query("SELECT r FROM AgentReview r " +
            "WHERE r.feedbackContent IS NOT NULL AND r.feedbackContent <> '' " +
            "ORDER BY r.createdAt DESC")
    List<AgentReview> findTop3ValidFeedbacks(Pageable pageable);

    long countByAgentProfileId(UUID agentProfileId);

    boolean existsByProposalId(Long proposalId);

    Optional<AgentReview> findByProposalId(Long proposalId);

    @Query("SELECT p FROM Proposal p " +
            "JOIN FETCH p.chatRoom cr " +
            "JOIN FETCH cr.agentProfile " +
            "WHERE cr.foreignerProfile.id = :foreignerId AND p.status IN :statuses " +
            "ORDER BY p.id DESC")
    List<Proposal> findLatestMatchedProposal(
            @Param("foreignerId") UUID foreignerId,
            @Param("statuses") List<ProposalStatus> statuses,
            Pageable pageable
    );
}
