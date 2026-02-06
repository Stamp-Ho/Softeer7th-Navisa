package com.navisa.be.recommendation.calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class FinalRecommendationCalculatorTest {

    private final FinalRecommendationCalculator calculator = new FinalRecommendationCalculator();

    @Test
    @DisplayName("기본 총점과 점수 단위(0~100)의 분배 점수를 곱해 최종 분야별 점수(sa)를 산출한다")
    void calculateSpecialtyScore_Success() {
        // given: 기본 총점 T=80, 최종 분배 점수 y=50.0
        double baseTotalScore = 80.0;
        double finalDistribution = 50.0;

        double result = calculator.calculateSpecialtyScore(baseTotalScore, finalDistribution);

        // then: 80.0 * 50.0 = 4000.0 (나눗셈 없이 점수 스케일 유지)
        assertThat(result).isEqualTo(4000.0, offset(0.001));
    }

    @Test
    @DisplayName("여러 전문 분야의 점수와 가중치를 곱해 최종 추천 점수(G)를 계산한다")
    void calculateFinalGrade_WeightedSum() {
        // given: 이미 산출된 분야별 점수(sa)와 사용자의 질문과의 유사도(wa)
        // sa는 이미 정규화가 끝난 최종 점수라고 가정
        Map<String, Double> specialtyScores = Map.of(
                "VISA_A", 40.0,
                "VISA_B", 60.0
        );
        Map<String, Double> similarities = Map.of(
                "VISA_A", 0.7, // A 분야 질문일 확률 70%
                "VISA_B", 0.3  // B 분야 질문일 확률 30%
        );

        // when: (40.0 * 0.7) + (60.0 * 0.3) = 28.0 + 18.0 = 46.0
        double finalGrade = calculator.calculateFinalGrade(specialtyScores, similarities);

        // then
        assertThat(finalGrade).isEqualTo(46.0, offset(0.001));
    }

    @Test
    @DisplayName("유사도 맵에 있는 분야가 점수 맵에 없는 경우 0점으로 처리하여 계산한다")
    void calculateFinalGrade_MissingScore() {
        // given
        Map<String, Double> specialtyScores = Map.of("VISA_A", 50.0);
        Map<String, Double> similarities = Map.of(
                "VISA_A", 0.5,
                "VISA_C", 0.5  // 점수 데이터가 없는 분야
        );

        // when: (50.0 * 0.5) + (0.0 * 0.5) = 25.0
        double finalGrade = calculator.calculateFinalGrade(specialtyScores, similarities);

        // then
        assertThat(finalGrade).isEqualTo(25.0, offset(0.001));
    }
}
