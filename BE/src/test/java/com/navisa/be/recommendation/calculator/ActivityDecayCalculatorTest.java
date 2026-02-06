package com.navisa.be.recommendation.calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class ActivityDecayCalculatorTest {

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
}
