package com.navisa.be.recommendation.calculator;

import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Step 4-1: 리뷰 신뢰도 계산
 * r_i,a = w_i,a / Σ(w_i,k)
 */
@Component
public class ReviewReliabilityCalculator {

    /**
     * 대상 리뷰의 신뢰도 비율 계산
     *
     * @param targetSimilarity 대상 리뷰 유사도
     * @param topSimilarities 상위 3개 유사도 리스트
     * @return 상대 비율 r_i,a
     */
    public double calculateRelativeRatio(double targetSimilarity, List<Double> topSimilarities) {
        double sum = topSimilarities.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (sum == 0.0) {
            return 0.0;
        }

        return targetSimilarity / sum;
    }
}
