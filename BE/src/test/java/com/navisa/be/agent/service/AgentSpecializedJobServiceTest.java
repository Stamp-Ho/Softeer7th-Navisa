package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.projection.AgentSpecializedJobWithCountProjection;
import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AgentSpecializedJobServiceTest {

    @InjectMocks
    private AgentSpecializedJobService agentSpecializedJobService;

    @Mock
    private AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;

    @Mock
    private AgentSpecializedJobRepository agentSpecializedJobRepository;

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

    @Test
    @DisplayName("배치 조회 시 count 기준 내림차순으로 정렬하여 agentId별 Top2를 반환한다")
    void getTop2SpecializedJobIdsBatch_shouldReturnTop2ByCount() {
        // given
        UUID agentId = UUID.randomUUID();
        Long job1Id = 1L;
        Long job2Id = 2L;
        Long job3Id = 3L;

        // job2가 count=5로 가장 높고, job1은 리뷰 없음(null), job3도 리뷰 없음(null)
        List<AgentSpecializedJobWithCountProjection> rows = List.of(
                mockProjection(agentId, job1Id, null),
                mockProjection(agentId, job2Id, 5),
                mockProjection(agentId, job3Id, null));

        when(agentSpecializedJobRepository.findAllWithCountByAgentIds(List.of(agentId)))
                .thenReturn(rows);

        // when
        Map<UUID, List<Long>> result = agentSpecializedJobService
                .getTop2SpecializedJobIdsBatch(List.of(agentId));

        // then
        assertThat(result).containsKey(agentId);
        assertThat(result.get(agentId))
                .as("null을 포함해서 count 높은 순(job2=5, job1=null)으로 Top2를 반환해야 한다")
                .hasSize(2)
                .containsExactly(job2Id, job1Id);
    }

    @Test
    @DisplayName("배치 조회 시 여러 행정사에 대해 각자의 Top2를 독립적으로 반환한다")
    void getTop2SpecializedJobIdsBatch_multipleAgents_eachReturnsIndependentTop2() {
        // given
        UUID agent1 = UUID.randomUUID();
        UUID agent2 = UUID.randomUUID();

        List<AgentSpecializedJobWithCountProjection> rows = List.of(
                mockProjection(agent1, 10L, 5),
                mockProjection(agent1, 20L, 3),
                mockProjection(agent1, 30L, 1),
                mockProjection(agent2, 40L, 2),
                mockProjection(agent2, 50L, null));

        when(agentSpecializedJobRepository.findAllWithCountByAgentIds(List.of(agent1, agent2)))
                .thenReturn(rows);

        // when
        Map<UUID, List<Long>> result = agentSpecializedJobService
                .getTop2SpecializedJobIdsBatch(List.of(agent1, agent2));

        // then
        assertThat(result.get(agent1))
                .as("agent1은 count 높은 순으로 Top2(10L, 20L)를 반환해야 한다")
                .hasSize(2)
                .containsExactly(10L, 20L);

        assertThat(result.get(agent2))
                .as("agent2는 등록된 job이 2개이므로 최대 2개 반환한다")
                .hasSize(2)
                .containsExactly(40L, 50L);
    }

    @Test
    @DisplayName("agentIds가 비어 있을 때 빈 Map을 반환한다")
    void getTop2SpecializedJobIdsBatch_emptyInput_returnsEmptyMap() {
        // when
        Map<UUID, List<Long>> result = agentSpecializedJobService.getTop2SpecializedJobIdsBatch(List.of());

        // then
        assertThat(result).isEmpty();
    }

    // interface projection을 Mockito mock으로 생성
    private AgentSpecializedJobWithCountProjection mockProjection(UUID agentId, Long jobCodeId, Integer count) {
        AgentSpecializedJobWithCountProjection projection = mock(AgentSpecializedJobWithCountProjection.class);
        when(projection.agentId()).thenReturn(agentId);
        when(projection.jobCodeId()).thenReturn(jobCodeId);
        when(projection.count()).thenReturn(count);
        return projection;
    }

    private AgentSpecializedJobSummary createMockSummary(Long jobCodeId) {
        AgentSpecializedJobSummary summary = mock(AgentSpecializedJobSummary.class);
        JobCode jobCode = mock(JobCode.class);

        when(jobCode.getId()).thenReturn(jobCodeId);
        when(summary.getJobCode()).thenReturn(jobCode);

        return summary;
    }
}
