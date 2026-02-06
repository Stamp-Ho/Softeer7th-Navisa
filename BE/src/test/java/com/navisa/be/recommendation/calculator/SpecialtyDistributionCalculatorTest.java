package com.navisa.be.recommendation.calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class SpecialtyDistributionCalculatorTest {

    private final SpecialtyDistributionCalculator calculator = new SpecialtyDistributionCalculator();

    @Test
    @DisplayName("전문 분야가 40개일 때 분배 점수는 정확히 40점(중앙값)이어야 한다")
    void calculateDistribution_CenterPoint() {
        // n = 40 이면 80 / (1 + e^0) = 80 / 2 = 40
        double result = calculator.calculateDistribution(40);

        assertThat(result).isEqualTo(40.0, offset(0.001));
    }

    @ParameterizedTest
    @DisplayName("전문 분야 개수에 따른 시그모이드 분배 점수 추이 검증")
    @CsvSource({
            "0, 78.55",   // n이 매우 적을 때 (최대치 80에 근접)
            "20, 70.45",  // n이 중앙값보다 적을 때
            "60, 9.55",   // n이 중앙값보다 많을 때
            "100, 0.15"   // n이 매우 많을 때 (최소치 0에 근접)
    })
    void calculateDistribution_Trend(int count, double expected) {
        double result = calculator.calculateDistribution(count);

        // 소수점 둘째 자리까지 근사치 확인
        assertThat(result).isCloseTo(expected, offset(0.05));
    }

    @Test
    @DisplayName("전문 분야가 늘어날수록 1개당 배분되는 점수는 줄어들어야 한다")
    void calculateDistribution_Monotonicity() {
        double scoreFor10 = calculator.calculateDistribution(10);
        double scoreFor20 = calculator.calculateDistribution(20);
        double scoreFor30 = calculator.calculateDistribution(30);

        assertThat(scoreFor10).isGreaterThan(scoreFor20);
        assertThat(scoreFor20).isGreaterThan(scoreFor30);
    }
}
