package com.navisa.be.agent.service;

import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentSpecializedJobServiceTest {

    @InjectMocks
    private AgentSpecializedJobService agentSpecializedJobService;

    @Mock
    private AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;

    @Test
    @DisplayName("상위 2개 전문직종 ID를 조회한다")
    void getTop2SpecializedJobIds_shouldReturnIds() {
        // given
        UUID agentId = UUID.randomUUID();

        AgentSpecializedJobSummary summary1 = createMockSummary(10L);
        AgentSpecializedJobSummary summary2 = createMockSummary(20L);

        when(agentSpecializedJobSummaryRepository.findTop2SummaryByAgentId(eq(agentId), any(PageRequest.class)))
                .thenReturn(List.of(summary1, summary2));

        // when
        List<Long> result = agentSpecializedJobService.getTop2SpecializedJobIds(agentId);

        // then
        assertThat(result).hasSize(2)
                .containsExactly(10L, 20L);
    }

    private AgentSpecializedJobSummary createMockSummary(Long jobCodeId) {
        AgentSpecializedJobSummary summary = org.mockito.Mockito.mock(AgentSpecializedJobSummary.class);
        JobCode jobCode = org.mockito.Mockito.mock(JobCode.class);

        when(jobCode.getId()).thenReturn(jobCodeId);
        when(summary.getJobCode()).thenReturn(jobCode);

        return summary;
    }
}
