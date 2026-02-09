package com.navisa.be.agent.scheduler;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.support.AgentProfileTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@EnableScheduling
class ActivityScoreSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private ActivityScoreScheduler activityScoreScheduler;

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ScheduledTaskHolder scheduledTaskHolder;

    @Test
    @DisplayName("활동 점수 갱신 메서드는 매일 새벽 4시에 실행되도록 설정되어 있다")
    void scheduler_shouldBeConfiguredFor4AM() {
        // given
        String expectedCron = "0 0 4 * * *";
        String targetMethodName = "updateAgentActivityScores";

        // when
        Set<ScheduledTask> scheduledTasks = scheduledTaskHolder.getScheduledTasks();

        // then
        boolean isConfiguredCorrectly = scheduledTasks.stream()
                .filter(task -> task.getTask() instanceof CronTask)
                .map(task -> (CronTask) task.getTask())
                .anyMatch(cronTask ->
                        cronTask.getExpression().equals(expectedCron) &&
                                cronTask.getRunnable().toString().contains(targetMethodName)
                );

        assertThat(isConfiguredCorrectly).isTrue();
    }

    @Test
    @DisplayName("신규 가입 후 한 번도 로그인하지 않은 행정사는 가입일 기준으로 점수가 자연 감소한다")
    void updateAgentActivityScores_shouldDecayBasedOnCreatedAt_whenLastLoginAtIsNull() {
        // given: 10일 전에 가입했지만 로그인을 한 번도 안 한 행정사 (초기 점수 100.0)
        User user = userTestFixture.createUser("newbie@test.com", UserType.VALID_AGENT);
        AgentProfile profile = agentProfileTestFixture.createAgentProfile("신입", "서울", user.getId());

        // when
        activityScoreScheduler.updateAgentActivityScores();

        // then
        AgentProfile updatedProfile = agentProfileRepository.findById(profile.getId()).orElseThrow();
        assertThat(updatedProfile.getActiveScore()).isEqualTo(99.8);
    }

    @Test
    @DisplayName("장기 미접속(10일)인 행정사는 미접속 페널티가 적용되어 점수가 감소한다")
    void updateAgentActivityScores_shouldApplyInactivityPenalty() {
        // given
        User user = userTestFixture.createUser("inactive@test.com", UserType.VALID_AGENT);
        AgentProfile profile = agentProfileTestFixture.createAgentProfile("잠수", "부산", user.getId());

        // when
        activityScoreScheduler.updateAgentActivityScores();

        // then
        AgentProfile updatedProfile = agentProfileRepository.findById(profile.getId()).orElseThrow();
        // 80점 이하이므로 활동 점수 로직 적용
        assertThat(updatedProfile.getActiveScore()).isLessThan(100.0);
    }
}
