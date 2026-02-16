package com.navisa.be.agent.service;

import com.navisa.be.agent.event.ReviewCreatedSpecializedJobEvent;
import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.service.JobCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentSpecializedJobSummaryService {

    private final JobCodeService jobCodeService;
    private final AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;

    @Transactional
    @EventListener
    public void updateBadgeSummary(ReviewCreatedSpecializedJobEvent event) {
        List<JobCode> jobCodes = jobCodeService.findAllById(event.specializedJobIds());

        for (JobCode jobCode : jobCodes) {
            AgentSpecializedJobSummary summary = agentSpecializedJobSummaryRepository.findByAgentIdAndJobCode(event.agentId(), jobCode)
                    .orElseGet(() -> new AgentSpecializedJobSummary(event.agentId(), jobCode));

            if (summary.getId() != null) {
                summary.incrementCount();
            }

            agentSpecializedJobSummaryRepository.save(summary);
        }
    }
}
