package com.navisa.be.recommendation.service;

import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewAccumulationServiceTest {

    @Mock
    private AgentSpecializedJobSummaryRepository summaryRepository;

    @InjectMocks
    private ReviewAccumulationService reviewAccumulationService;

    @Test
    @DisplayName("리뷰 가중치 누적 시 Repository의 Native UPSERT 메서드가 정확한 인자와 함께 호출되어야 한다")
    void accumulateReviewWeight_ShouldInvokeUpsertWithCorrectParams() {
        // given
        UUID agentId = UUID.randomUUID();
        Long jobCodeId = 10L;
        double relativeRatio = 0.75;

        // when
        reviewAccumulationService.accumulateReviewWeight(agentId, jobCodeId, relativeRatio);

        // then
        // 네이티브 쿼리를 실행하는 upsertReliability가 인자 그대로 호출되었는지 검증
        verify(summaryRepository, times(1))
                .upsertReliability(agentId, jobCodeId, relativeRatio);
    }
}
