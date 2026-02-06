package com.navisa.be.recommendation.calculator;

import org.springframework.stereotype.Component;

/**
 * Step 5: 리뷰 가산점 계산
 */
@Component
public class ReviewBonusCalculator {

    /**
     * 리뷰 누적 신뢰도에 따른 보너스 계수 계산
     *
     * g(z_a) = 1 - e^(-0.035 * z_a)
     *
     * @param zA 누적 리뷰 신뢰도
     * @return 보너스 계수 g(z_a)
     */
    public double calculateBonusFactor(double zA) {
        return 1.0 - Math.exp(-0.035 * zA);
    }

    /**
     * 최종 분배 점수 계산
     *
     * y(n, z_a) = y(n) + (100 - y(n)) × g(z_a)
     *
     * @param baseDistribution Step3에서 계산된 기본 분배 점수 y(n)
     * @param bonusFactor 리뷰 보너스 계수 g(z_a)
     * @return 리뷰 가산이 반영된 최종 분배 점수
     */
    public double calculateFinalDistribution(
            double baseDistribution,
            double bonusFactor
    ) {
        return baseDistribution + (100.0 - baseDistribution) * bonusFactor;
    }
}
