package com.navisa.be.recommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAccumulationService {

    private final RedisTemplate<String, Double> doubleRedisTemplate;

    // 데이터 유효 기간
    private static final Duration DATA_TTL = Duration.ofDays(1);

    /**
     * Step 4-2: 누적 리뷰 신뢰도(zA) 갱신
     */
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public void accumulateReviewWeight(UUID agentId, Long jobCodeId, double relativeRatio) {
        // Redis Key 생성
        String key = String.format("matching:sandbox:%s:%d", agentId, jobCodeId);

        try {
            // Redis 원자적 가산 (Lost Update 해결)
            doubleRedisTemplate.opsForValue().increment(key, relativeRatio);
            doubleRedisTemplate.expire(key, DATA_TTL);

            log.debug("[Redis Success] Key: {}, Added: {}", key, relativeRatio);
        } catch (Exception e) {
            log.error("[Redis Error] 가중치 누적 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }
}
