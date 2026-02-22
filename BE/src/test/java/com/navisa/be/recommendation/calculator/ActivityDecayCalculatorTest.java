package com.navisa.be.recommendation.calculator;

import com.navisa.be.recommendation.service.ReviewAccumulationService;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ActivityDecayCalculatorTest extends IntegrationTestSupport {

    @Autowired
    private ReviewAccumulationService reviewAccumulationService;

    @MockitoBean(name = "doubleRedisTemplate")
    private RedisTemplate<String, Double> mockRedisTemplate;

    private final ActivityDecayCalculator calculator = new ActivityDecayCalculator();

    @Test
    @DisplayName("현재 점수가 80~100이면 MAX_ACTIVITY_SCORE까지 자연 감소")
    void calculateDailyScore_shouldApplyMaxScoreDecay() {
        double currentScore = 99.8;

        double updatedScore = calculator.calculateDailyScore(currentScore, 0, 0);

        // 하루 0.2씩 감소
        assertThat(updatedScore).isEqualTo(99.6);
    }

    @Test
    @DisplayName("점수는 항상 MIN_SCORE는 10이고, 이를 벗어나면 보정된다.")
    void calculateDailyScore_shouldRespectMinMaxScore() {
        double resultLow = calculator.calculateDailyScore(5.0, 0, 0);   // 너무 낮은 점수
        double resultHigh = calculator.calculateDailyScore(90.0, 0, 0); // 너무 높은 점수

        assertThat(resultLow).isEqualTo(10.0);              // MIN_SCORE 보정
        assertThat(resultHigh).isLessThanOrEqualTo(89.8);  // MAX_ACTIVITY_SCORE 보정
    }

    @Test
    @DisplayName("연속 접속 15일째에는 3일 단위 보너스가 5회 누적되어 2.5점이 증가해야 한다")
    void calculateDailyScore_shouldAccumulateBonusForLongReconnect() {
        // given: 현재 점수 70.0, 연속 접속 15일
        double currentScore = 70.0;
        int daysSinceReconnect = 15;

        // when: 70.0 + (15/3 * 0.5) = 70.0 + 2.5 = 72.5
        double updatedScore = calculator.calculateDailyScore(currentScore, 0, daysSinceReconnect);

        // then
        assertThat(updatedScore).isEqualTo(72.5, offset(0.001));
    }

    @Test
    @DisplayName("미접속/재접속 복합 조건 적용 시 감소와 증가가 동시에 반영된다")
    void calculateDailyScore_shouldHandleMultipleConditions() {
        double currentScore = 75.0;
        int consecutiveInactiveDays = 10; // 5일 단위 2회 감소 (-0.4)
        int daysSinceReconnect = 6;        // 3일 단위 2회 증가 (+1.0)

        double updatedScore = calculator.calculateDailyScore(currentScore, consecutiveInactiveDays, daysSinceReconnect);

        // 75.0 - 0.4 + 1.0 = 75.6
        assertThat(updatedScore).isEqualTo(75.6, offset(0.001));
    }

    @Test
    @DisplayName("연속 미접속 5일 단위로 점수가 0.2씩 누적 감소한다")
    void calculateDailyScore_shouldDecreaseByInactivityFactor() {
        double currentScore = 78.0;
        int consecutiveInactiveDays = 14; // 14/5 = 2회 적용

        double updatedScore = calculator.calculateDailyScore(currentScore, consecutiveInactiveDays, 0);

        // 78.0 - (2 * 0.2) = 77.6
        assertThat(updatedScore).isEqualTo(77.6, offset(0.001));
    }

    @Test
    @DisplayName("멀티 스레드 환경에서 서로 다른 스레드가 점수를 갱신할 때 발생하는 갱신 손실(Lost Update) 증명")
    void verifyLostUpdateSimulation() throws InterruptedException {
        // given
        // 현재 점수 80.0인 행정사가 있다고 가정
        final double initialScore = 80.0;

        // 두 개의 스레드가 거의 동시에 점수를 수정하려 함
        // 스레드 A (스케줄러): 재접속 보너스 6일치(+1.0)를 계산 중
        // 스레드 B (리뷰/로그인): 재접속 보너스 9일치(+1.5)를 계산 중

        int numberOfThreads = 2;
        java.util.concurrent.ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(numberOfThreads);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(numberOfThreads);

        // 실제 DB 상황을 모사하기 위한 가짜 공유 엔티티
        class AgentProxy { double score = initialScore; }
        final AgentProxy sharedAgent = new AgentProxy();

        // when
        // Thread A: 스케줄러 시뮬레이션
        executorService.submit(() -> {
            try {
                double current = sharedAgent.score; // (1) 공유 자원 읽기
                Thread.sleep(100); // 연산 및 네트워크 지연 시뮬레이션
                sharedAgent.score = calculator.calculateDailyScore(current, 0, 6); // (2) 80.0 + 1.0 연산 후 쓰기
            } catch (Exception e) { e.printStackTrace(); }
            finally { latch.countDown(); }
        });

        // Thread B: 실시간 활동 시뮬레이션
        executorService.submit(() -> {
            try {
                double current = sharedAgent.score; // (1) 공유 자원 읽기
                Thread.sleep(100); // 연산 및 네트워크 지연 시뮬레이션
                sharedAgent.score = calculator.calculateDailyScore(current, 0, 9); // (2) 80.0 + 1.5 연산 후 쓰기
            } catch (Exception e) { e.printStackTrace(); }
            finally { latch.countDown(); }
        });

        latch.await();

        // then
        // 정상적인 원자적 연산이라면 결과는 82.5 (80.0 + 1.0 + 1.5)여야 함
        // 그러나 Read-Modify-Write 패턴에서는 나중에 끝난 스레드가 앞선 수정을 덮어씀
        double expectedScore = 82.5;

        System.out.println("--- 동시성 경합 결과 ---");
        System.out.println("정상 기대 점수: " + expectedScore);
        System.out.println("실제 남은 점수: " + sharedAgent.score);

        // 실제 점수를 비교
        assertThat(sharedAgent.score).isNotEqualTo(expectedScore);
    }

    @Test
    @DisplayName("Redis 연결 실패 시 @Retryable 재시도 후 성공 검증")
    void verify_RetryMechanism_SuccessAfterRetries() {
        ValueOperations<String, Double> mockOps = mock(ValueOperations.class);
        when(mockRedisTemplate.opsForValue()).thenReturn(mockOps);

        // 2회 실패 후 3회차 성공 설정
        when(mockOps.increment(anyString(), anyDouble()))
                .thenThrow(new RuntimeException("Error 1"))
                .thenThrow(new RuntimeException("Error 2"))
                .thenReturn(80.5);

        // when: 호출 (프록시가 내부적으로 3번 시도함)
        reviewAccumulationService.accumulateReviewWeight(UUID.randomUUID(), 1L, 0.5);

        // then: 최종적으로 increment가 3번 호출되었는지를 RedisTemplate(Mock) 기준으로 검증
        verify(mockOps, times(3)).increment(anyString(), anyDouble());
    }
}
