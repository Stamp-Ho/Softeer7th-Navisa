package com.navisa.be.agent.service;

import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadge;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.repository.AgentBadgeRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
@RequiredArgsConstructor
@Service
public class AgentReviewCrudService {

    private final AgentReviewRepository agentReviewRepository;
    private final AgentBadgeRepository agentBadgeRepository;

    public void createAgentReview(UUID agentId, UUID foreignerId, Long proposalId, List<Badge> badges) {
        // 리뷰와 리뷰 뱃지를 저장
        AgentReview agentReview = new AgentReview(agentId, foreignerId, proposalId);
        agentReviewRepository.save(agentReview);

        List<AgentBadge> agentBadges = badges.stream()
                .map(badge -> new AgentBadge(badge, agentReview))
                .toList();
        agentBadgeRepository.saveAll(agentBadges);
    }

    public void updateAgentFeedback(Long proposalId, String content) {
        AgentReview agentReview = agentReviewRepository.findByProposalId(proposalId)
                .orElseThrow(() -> new AgentException(ResponseStatus.REVIEW_NOT_FOUND));

        if (agentReview.getFeedbackContent() != null) {
            throw new AgentException(ResponseStatus.FEEDBACK_ALREADY_EXISTS);
        }

        agentReview.updateFeedback(content);
    }

    public boolean existsByProposalId(Long proposalId) {
        return agentReviewRepository.existsByProposalId(proposalId);
    }
}
