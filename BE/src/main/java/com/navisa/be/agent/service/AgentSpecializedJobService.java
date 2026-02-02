package com.navisa.be.agent.service;

import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AgentSpecializedJobService {

    private final AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;

    // 특정 행정사의 상위 2개 직종코드 Id 조회
    @Transactional(readOnly = true)
    public List<Long> getTop2SpecializedJobIds(UUID agentId) {
        List<AgentSpecializedJobSummary> summaries = agentSpecializedJobSummaryRepository.findTop2SummaryByAgentId(
                agentId, PageRequest.of(0, 2)
        );

        return summaries.stream()
                .map(summary -> summary.getJobCode().getId())
                .toList();
    }
}
