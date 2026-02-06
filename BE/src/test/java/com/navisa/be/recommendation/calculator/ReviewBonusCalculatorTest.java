package com.navisa.be.recommendation.calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class ReviewBonusCalculatorTest {

    private final ReviewBonusCalculator calculator = new ReviewBonusCalculator();

    @ParameterizedTest
    @DisplayName("누적 신뢰도(zA)에 따른 보너스 계수 g(zA) 계산 검증")
    @CsvSource({
            "0.0, 0.0",      // 리뷰가 없으면 가산점 계수는 0
            "10.0, 0.2953",  // zA=10일 때 약 0.295
            "20.0, 0.5034",  // zA=20일 때 약 0.503
            "50.0, 0.8262",  // zA=50일 때 약 0.826
            "100.0, 0.9698"  // zA가 높을수록 1.0에 수렴
    })
    void calculateBonusFactor_Trend(double zA, double expected) {
        double result = calculator.calculateBonusFactor(zA);

        // 지수 연산 오차를 고려하여 0.001 오차 범위 허용
        assertThat(result).isCloseTo(expected, offset(0.001));
    }

    @Test
    @DisplayName("최종 분배 점수는 0~100 사이의 점수(Score)를 반환해야 한다")
    void calculateFinalDistribution_Success() {
        // given: 기본 분배 점수 y(n) = 40.0, 보너스 계수 g(za) = 0.5
        // 공식: 40 + (100 - 40) * 0.5 = 40 + 30 = 70.0
        double baseDistribution = 40.0;
        double bonusFactor = 0.5;

        // when
        double finalScore = calculator.calculateFinalDistribution(baseDistribution, bonusFactor);

        // then
        assertThat(finalScore).isEqualTo(70.0, offset(0.001));
    }

    @Test
    @DisplayName("보너스 계수가 1에 가까워질수록 최종 점수는 100점에 수렴해야 한다")
    void calculateFinalDistribution_MaxConvergence() {
        // given: 기본 점수가 낮더라도 보너스 계수가 높으면 점수가 크게 상승
        // 10 + (90 * 0.99) = 99.1
        double baseDistribution = 10.0;
        double bonusFactor = 0.99;

        // when
        double finalScore = calculator.calculateFinalDistribution(baseDistribution, bonusFactor);

        // then: 100점에 근접한 점수 확인
        assertThat(finalScore).isCloseTo(100.0, offset(1.0));
        assertThat(finalScore).isGreaterThan(baseDistribution);
    }
}
