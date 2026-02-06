package com.navisa.be.recommendation.calculator;

import org.springframework.stereotype.Component;

/**
 * Step 3: 전문 분야 분배 점수 계산
 * 실행 시점: 행정사 정보 입력 / 수정
 */
@Component
public class SpecialtyDistributionCalculator {

    /**
     * 전문 분야 점수 분배 계산
     *
     * @param specialtyCount 등록한 전문 분야 개수 (n)
     * @return 분배 점수 y(n)
     */
    public double calculateDistribution(int specialtyCount) {
        // 시그모이드 형태로 점수 분배
        return 80.0 / (1.0 + Math.exp(0.1 * (specialtyCount - 40)));
    }
}
