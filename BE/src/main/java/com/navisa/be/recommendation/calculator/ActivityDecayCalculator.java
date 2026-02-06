package com.navisa.be.recommendation.calculator;

import org.springframework.stereotype.Component;

@Component
public class ActivityDecayCalculator {

    private static final double MAX_ACTIVITY_SCORE = 80.0;
    private static final double MIN_SCORE = 10.0;

    /**
     * Step 2: 행정사 기초 총점 산정
     * 실행 시점: 매일 04시
     *
     * @param currentScore 현재 점수
     * @param consecutiveInactiveDays 연속 미접속 일수
     * @param daysSinceReconnect 재접속 후 경과 일수
     * @return 갱신된 점수
     */
    public double calculateDailyScore(
            double currentScore,
            int consecutiveInactiveDays,
            int daysSinceReconnect
    ) {
        double score = currentScore;

        /* =========================
         * 1. 자연 감소 (100 → 80)
         * 현재 점수가 80 이상이면 매일 0.2씩 감소
         * ========================= */
        if (score > MAX_ACTIVITY_SCORE) {
            score -= 0.2; // 하루 단위 감소
            return score; // 자연 감소 중에는 활동 점수 및 범위 제한 적용 안 함
        }

        /* =========================
         * 2. 활동 점수 (80 이하)
         * ========================= */
        // 연속 미접속 5일 단위 감소
        int inactivityFactor = consecutiveInactiveDays / 5;
        score -= inactivityFactor * 0.2;

        // 재접속 후 3일 단위 증가
        int reconnectPeriods = daysSinceReconnect / 3;
        score += (reconnectPeriods * 0.5);

        /* =========================
         * 3. 점수 범위 제한
         * 최소 10점, 최대 80점
         * ========================= */
        if (score > MAX_ACTIVITY_SCORE) {
            score = MAX_ACTIVITY_SCORE;
        }
        if (score < MIN_SCORE) {
            score = MIN_SCORE;
        }

        return score;
    }
}
