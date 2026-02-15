package com.navisa.be.agent.service;

import com.navisa.be.agent.event.ReviewCreatedSpecializedJobEvent;
import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.repository.JobCodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecializedJobSummaryServiceTest {

    @InjectMocks
    private SpecializedJobSummaryService specializedJobSummaryService;

    @Mock
    private JobCodeRepository jobCodeRepository;

    @Mock
    private AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;

    @Test
    @DisplayName("이벤트를 받으면 JobCode를 조회하고 요약을 업데이트한다")
    void updateBadgeSummary_shouldUpdateSummaries() throws Exception {
        // given
        UUID agentId = UUID.randomUUID();
        List<Long> jobIds = List.of(1L, 2L);
        List<Double> ratios = List.of(0.8, 0.2);
        ReviewCreatedSpecializedJobEvent event = new ReviewCreatedSpecializedJobEvent(agentId, jobIds, ratios);

        java.lang.reflect.Constructor<JobCode> constructor = JobCode.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        JobCode jobCode1 = constructor.newInstance();
        JobCode jobCode2 = constructor.newInstance();

        ReflectionTestUtils.setField(jobCode1, "id", 1L);
        ReflectionTestUtils.setField(jobCode2, "id", 2L);

        when(jobCodeRepository.findAllById(jobIds)).thenReturn(List.of(jobCode1, jobCode2));

        // Case 1: Existing summary for jobCode1
        AgentSpecializedJobSummary existingSummary = mock(AgentSpecializedJobSummary.class);
        when(existingSummary.getId()).thenReturn(100L); // simulate persisted
        when(agentSpecializedJobSummaryRepository.findByAgentIdAndJobCode(agentId, jobCode1))
                .thenReturn(Optional.of(existingSummary));

        when(agentSpecializedJobSummaryRepository.findByAgentIdAndJobCode(agentId, jobCode2))
                .thenReturn(Optional.empty());

        // when
        specializedJobSummaryService.updateBadgeSummary(event);

        // then
        verify(existingSummary, times(1)).incrementCount();
        verify(agentSpecializedJobSummaryRepository).save(existingSummary);

        verify(agentSpecializedJobSummaryRepository, times(2)).save(any(AgentSpecializedJobSummary.class));
    }
}
