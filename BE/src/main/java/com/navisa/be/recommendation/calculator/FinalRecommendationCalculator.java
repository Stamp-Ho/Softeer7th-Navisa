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
    public double calculateFinalGradeByLongId(Map<Long, Double> specialtyScores, Map<Long, Double> similarities) {
        double result = 0.0;

        // 사용자의 관심 분야(similarities)를 기준으로 루프
        for (Long jobId : similarities.keySet()) {
            double wa = similarities.getOrDefault(jobId, 0.0);
            double sa = specialtyScores.getOrDefault(jobId, 0.0);

            // 사용자의 니즈(wa)와 행정사의 전문성(sa)을 곱해서 합산
            result += (wa * sa);
        }

        return result;
    }

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
