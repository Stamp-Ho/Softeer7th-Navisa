package com.navisa.be.agent.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class AgentSpecializedJobServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private AgentSpecializedJobService agentSpecializedJobService;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Test
    @DisplayName("리뷰가 전혀 없어도 등록된 specializedJob 앞 2개를 반환한다")
    void getTop2_noReview_returnRegisteredJobs() {
        // given
        User user = userTestFixture.createUser("agent-no-review@test.com", UserType.VALID_AGENT);
        AgentProfile profile = agentProfileTestFixture.createAgentProfile("행정사A", "서울", user.getId());

        JobCode job1 = agentProfileTestFixture.createJobCode("C001", "직무1");
        JobCode job2 = agentProfileTestFixture.createJobCode("C002", "직무2");
        JobCode job3 = agentProfileTestFixture.createJobCode("C003", "직무3");

        agentProfileTestFixture.createAgentSpecializedJob(profile, job1);
        agentProfileTestFixture.createAgentSpecializedJob(profile, job2);
        agentProfileTestFixture.createAgentSpecializedJob(profile, job3);

        // when (Summary 없음 → LEFT JOIN 결과에서 reviewScore는 모두 null)
        Map<UUID, List<Long>> result = agentSpecializedJobService
                .getTop2SpecializedJobIdsBatch(List.of(profile.getId()));

        // then
        List<Long> ids = result.get(profile.getId());
        assertThat(ids)
                .as("리뷰가 없어도 등록된 specializedJob에서 Top2를 채워야 한다")
                .hasSize(2)
                .containsAnyOf(job1.getId(), job2.getId(), job3.getId());
    }

    @Test
    @DisplayName("리뷰(Summary)가 1개만 있을 때 리뷰 기반 1개 + fallback 1개로 2개를 반환한다")
    void getTop2_oneReview_fillsWithFallback() {
        // given
        User user = userTestFixture.createUser("agent-one-review@test.com", UserType.VALID_AGENT);
        AgentProfile profile = agentProfileTestFixture.createAgentProfile("행정사B", "부산", user.getId());

        JobCode job1 = agentProfileTestFixture.createJobCode("D001", "직무A");
        JobCode job2 = agentProfileTestFixture.createJobCode("D002", "직무B");

        agentProfileTestFixture.createAgentSpecializedJob(profile, job1);
        agentProfileTestFixture.createAgentSpecializedJob(profile, job2);

        // job1에 대해서만 Summary 존재 (리뷰 1개)
        agentProfileTestFixture.createAgentSpecializedJobSummary(profile.getId(), job1);

        // when
        Map<UUID, List<Long>> result = agentSpecializedJobService
                .getTop2SpecializedJobIdsBatch(List.of(profile.getId()));

        // then
        List<Long> ids = result.get(profile.getId());
        assertThat(ids)
                .as("Summary 1개 + fallback으로 총 2개여야 한다")
                .hasSize(2)
                .contains(job1.getId(), job2.getId());
    }

    @Test
    @DisplayName("리뷰(Summary)가 2개 이상이면 점수 높은 Top2를 반환한다")
    void getTop2_withEnoughReviews_returnsTopByScore() {
        // given
        User user = userTestFixture.createUser("agent-two-review@test.com", UserType.VALID_AGENT);
        AgentProfile profile = agentProfileTestFixture.createAgentProfile("행정사C", "인천", user.getId());

        JobCode job1 = agentProfileTestFixture.createJobCode("E001", "직무X");
        JobCode job2 = agentProfileTestFixture.createJobCode("E002", "직무Y");
        JobCode job3 = agentProfileTestFixture.createJobCode("E003", "직무Z");

        agentProfileTestFixture.createAgentSpecializedJob(profile, job1);
        agentProfileTestFixture.createAgentSpecializedJob(profile, job2);
        agentProfileTestFixture.createAgentSpecializedJob(profile, job3);

        // Summary 3개 모두 존재 (점수 차이 없음 → 순서 무관, 2개만 나오면 됨)
        agentProfileTestFixture.createAgentSpecializedJobSummary(profile.getId(), job1);
        agentProfileTestFixture.createAgentSpecializedJobSummary(profile.getId(), job2);
        agentProfileTestFixture.createAgentSpecializedJobSummary(profile.getId(), job3);

        // when
        Map<UUID, List<Long>> result = agentSpecializedJobService
                .getTop2SpecializedJobIdsBatch(List.of(profile.getId()));

        // then
        List<Long> ids = result.get(profile.getId());
        assertThat(ids)
                .as("Summary 3개 중 상위 2개만 반환해야 한다")
                .hasSize(2)
                .allMatch(id -> List.of(job1.getId(), job2.getId(), job3.getId()).contains(id));
    }

    @Test
    @DisplayName("여러 행정사를 배치 조회할 때 각자 최대 2개씩 반환한다")
    void getTop2_batchMultipleAgents_eachReturnsUpTo2() {
        // given
        User user1 = userTestFixture.createUser("agent-batch1@test.com", UserType.VALID_AGENT);
        User user2 = userTestFixture.createUser("agent-batch2@test.com", UserType.VALID_AGENT);

        AgentProfile profile1 = agentProfileTestFixture.createAgentProfile("행정사D", "서울", user1.getId());
        AgentProfile profile2 = agentProfileTestFixture.createAgentProfile("행정사E", "대전", user2.getId());

        JobCode jobA = agentProfileTestFixture.createJobCode("F001", "배치직무A");
        JobCode jobB = agentProfileTestFixture.createJobCode("F002", "배치직무B");
        JobCode jobC = agentProfileTestFixture.createJobCode("F003", "배치직무C");

        // profile1: specializedJob 2개, 리뷰 없음
        agentProfileTestFixture.createAgentSpecializedJob(profile1, jobA);
        agentProfileTestFixture.createAgentSpecializedJob(profile1, jobB);

        // profile2: specializedJob 1개, 리뷰 없음
        agentProfileTestFixture.createAgentSpecializedJob(profile2, jobC);

        // when
        Map<UUID, List<Long>> result = agentSpecializedJobService.getTop2SpecializedJobIdsBatch(
                List.of(profile1.getId(), profile2.getId()));

        // then
        assertThat(result.get(profile1.getId()))
                .as("profile1은 job 2개를 모두 반환해야 한다")
                .hasSize(2)
                .containsExactlyInAnyOrder(jobA.getId(), jobB.getId());

        assertThat(result.get(profile2.getId()))
                .as("profile2는 등록된 job이 1개뿐이므로 최대 1개만 반환한다")
                .hasSize(1)
                .contains(jobC.getId());
    }
}
