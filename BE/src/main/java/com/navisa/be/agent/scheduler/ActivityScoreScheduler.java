package com.navisa.be.agent.scheduler;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.recommendation.calculator.ActivityDecayCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityScoreScheduler {

    private final AgentProfileRepository agentProfileRepository;
    private final ActivityDecayCalculator activityDecayCalculator;

    @Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시
    @Transactional
    public void updateAgentActivityScores() {
        log.info("행정사 활동 점수 갱신 스케줄러 시작");

        List<AgentProfile> profiles = agentProfileRepository.findAllValidAgentProfiles();
        ZonedDateTime now = ZonedDateTime.now();

        for (AgentProfile profile : profiles) {
            // lastLoginAt이 없으면 가입일을 기준으로 미접속 일수 계산
            ZonedDateTime lastActivityDate = (profile.getLastLoginAt() != null)
                    ? profile.getLastLoginAt()
                    : profile.getCreatedAt().atZone(ZoneId.systemDefault());

            int inactiveDays = calculateDaysBetween(lastActivityDate, now);
            int daysSinceReconnect = (profile.getReconnectedAt() != null)
                    ? calculateDaysBetween(profile.getReconnectedAt(), now)
                    : -1;

            double newScore = activityDecayCalculator.calculateDailyScore(
                    profile.getActiveScore(),
                    inactiveDays,
                    daysSinceReconnect
            );

            profile.updateActiveScore(newScore);
        }

        log.info("행정사 활동 점수 갱신 완료 (총 {}명)", profiles.size());
    }

    private int calculateDaysBetween(ZonedDateTime startAt, ZonedDateTime endAt) {
        if (startAt == null) return 0;
        int days = (int) Duration.between(startAt, endAt).toDays();
        return Math.max(days, 0);
    }
}
