package com.navisa.be.recommendation.calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class ReviewReliabilityCalculatorTest {

    private final ReviewReliabilityCalculator calculator = new ReviewReliabilityCalculator();

    @Test
    @DisplayName("상위 3개 유사도 합 대비 대상 유사도의 비율이 정확히 계산된다")
    void calculateRelativeRatio_Success() {
        // given
        double targetSimilarity = 0.9;
        // 상위 3개 유사도 리스트 (대상 유사도 0.9 포함)
        List<Double> top3Similarities = List.of(0.9, 0.6, 0.3);
        // sum = 1.8, target = 0.9 -> ratio = 0.5 (0.9 / 1.8)

        // when
        double result = calculator.calculateRelativeRatio(targetSimilarity, top3Similarities);

        // then
        assertThat(result).isEqualTo(0.5, offset(0.001));
    }

    @Test
    @DisplayName("유사도의 합이 0인 경우(나눗셈 0 방지) 0.0을 반환한다")
    void calculateRelativeRatio_ReturnZeroWhenSumIsZero() {
        // given
        double targetSimilarity = 0.0;
        List<Double> top3Similarities = List.of(0.0, 0.0, 0.0);

        // when
        double result = calculator.calculateRelativeRatio(targetSimilarity, top3Similarities);

        // then
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("대상 유사도가 가장 낮을 때도 상위 3개 합 대비 비율을 계산한다")
    void calculateRelativeRatio_LowSimilarityCase() {
        // given
        double targetSimilarity = 0.2;
        List<Double> top3Similarities = List.of(0.5, 0.3, 0.2); // sum = 1.0

        // when
        double result = calculator.calculateRelativeRatio(targetSimilarity, top3Similarities);

        // then
        assertThat(result).isEqualTo(0.2, offset(0.001));
    }
}
