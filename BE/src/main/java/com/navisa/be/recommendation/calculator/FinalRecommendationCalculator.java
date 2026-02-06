package com.navisa.be.recommendation.calculator;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Step 6: 최종 추천 점수 계산
 */
@Component
public class FinalRecommendationCalculator {

    /**
     * 전문 분야별 점수 계산
     *
     * s_a = T × y(n, z_a)
     *
     * @param baseTotalScore Step2까지 계산된 기본 총점 T
     * @param finalDistribution Step5에서 계산된 최종 분배 점수 y(n, z_a)
     * @return 전문 분야별 점수 s_a
     */
    public double calculateSpecialtyScore(
            double baseTotalScore,
            double finalDistribution
    ) {
        return baseTotalScore * finalDistribution;
    }

    /**
     * 최종 추천 점수 계산
     *
     * G = Σ(w_a × s_a)
     *
     * @param specialtyScores 전문 분야별 점수 s_a
     * @param similarities 전문 분야별 유사도 가중치 w_a
     * @return 최종 추천 점수 G
     */
    public double calculateFinalGrade(
            Map<String, Double> specialtyScores,
            Map<String, Double> similarities
    ) {
        double result = 0.0;

        for (String specialty : similarities.keySet()) {
            double weight = similarities.getOrDefault(specialty, 0.0);
            double score = specialtyScores.getOrDefault(specialty, 0.0);
            result += weight * score;
        }

        return result;
    }
}
