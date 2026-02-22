package com.navisa.be.recommendation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewAccumulationServiceTest {

    @Mock
    private RedisTemplate<String, Double> doubleRedisTemplate;

    @Mock
    private ValueOperations<String, Double> valueOperations;

    @InjectMocks
    private ReviewAccumulationService reviewAccumulationService;

    @Test
    @DisplayName("리뷰 가중치 누적 시 Redis의 원자적 가산 연산과 TTL 설정이 호출되어야 한다")
    void accumulateReviewWeight_ShouldInvokeRedisAtomicOps() {
        // given
        UUID agentId = UUID.randomUUID();
        Long jobCodeId = 10L;
        double relativeRatio = 0.75;
        String expectedKey = String.format("matching:sandbox:%s:%d", agentId, jobCodeId);

        given(doubleRedisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        reviewAccumulationService.accumulateReviewWeight(agentId, jobCodeId, relativeRatio);

        // then
        // 원자적 가산(increment)이 정확한 키와 값으로 호출되었는가?
        verify(valueOperations, times(1))
                .increment(eq(expectedKey), eq(relativeRatio));

        // 스토리지 오염 방지를 위한 TTL(1일) 설정이 호출되었는가?
        verify(doubleRedisTemplate, times(1))
                .expire(eq(expectedKey), eq(Duration.ofDays(1)));
    }
}
