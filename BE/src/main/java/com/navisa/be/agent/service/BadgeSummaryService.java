package com.navisa.be.agent.service;

import com.navisa.be.agent.event.ReviewCreatedEvent;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.repository.AgentBadgeSummaryRepository;
import com.navisa.be.agent.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeSummaryService {

    private final AgentBadgeSummaryRepository summaryRepository;
    private final BadgeRepository badgeRepository;

    @Transactional
    @EventListener
    public void updateBadgeSummary(ReviewCreatedEvent event) {
        List<Badge> badges = badgeRepository.findAllById(event.badgeIds());

        for (Badge badge : badges) {
            AgentBadgeSummary summary = summaryRepository.findByAgentIdAndBadge(event.agentId(), badge)
                    .orElseGet(() -> new AgentBadgeSummary(event.agentId(), badge));

            if (summary.getId() != null) {
                summary.incrementCount();
            }

            summaryRepository.save(summary);
        }
    }
}
