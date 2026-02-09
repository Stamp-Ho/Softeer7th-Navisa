package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.FeedbackResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.agent.service.AgentHomeService;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AgentHomeServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentHomeService agentHomeService;

    @Autowired
    private AgentReviewRepository agentReviewRepository;

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private AgentSpecializedJobRepository specializedJobCodeRepository;

    @DisplayName("최신순으로 등록된 행정사 리뷰 3개를 조회하고 작성자 정보를 매핑한다.")
    @Test
    void getLatestFeedbacks_Success() {
        // given
        UUID mockUserId = UUID.randomUUID();
        AgentProfile savedProfile = saveAgentProfile(mockUserId, "김행정", "https://image.com/profile1");
        UUID actualAgentId = savedProfile.getId();

        for (int i = 1; i <= 5; i++) {
            AgentReview review = new AgentReview(
                    actualAgentId,
                    mockUserId,
                    (long) i,
                    "피드백 내용 " + i,
                    new double[] { 0.8, 0.9 });

            agentReviewRepository.save(review);

            org.springframework.test.util.ReflectionTestUtils.setField(review, "createdAt",
                    java.time.LocalDateTime.now().plusSeconds(i));
            agentReviewRepository.saveAndFlush(review);
        }

        // when
        List<FeedbackResponse> result = agentHomeService.getLatestFeedbacks();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).feedbackContent()).isEqualTo("피드백 내용 5");
        assertThat(result.get(0).writerName()).isEqualTo("김행정");
    }

    @DisplayName("등록된 리뷰가 하나도 없을 경우 AGENT_REVIEW_NOT_FOUND 예외가 발생한다.")
    @Test
    void getLatestFeedbacks_NotFound() {
        // given (리뷰를 저장하지 않음)

        // when & then
        assertThatThrownBy(() -> agentHomeService.getLatestFeedbacks())
                .isInstanceOf(AgentException.class)
                .hasMessageContaining(ResponseStatus.AGENT_REVIEW_NOT_FOUND.getMessage());
    }

    @DisplayName("비로그인 상태(email=null)에서 랜덤 카드 조회 시 전문분야는 null로 반환된다.")
    @Test
    void getRandomAgentCards_Guest_Success() {
        // given
        createAgentProfiles(12);
        String emptyEmail = "";

        // when
        List<AgentCardResponse> result = agentHomeService.getRandomAgentCards(emptyEmail);

        // then
        assertThat(result).hasSize(12);
        assertThat(result.get(0).agentSpecialityTop2()).isNull(); // 비로그인 시 null 확인
    }

    @DisplayName("로그인 상태(email 존재)에서 랜덤 카드 조회 시 전문분야 데이터가 포함된다.")
    @Test
    void getRandomAgentCards_Login_Success() {
        // given
        createAgentProfiles(12);
        String loginEmail = "test@example.com";

        // when
        List<AgentCardResponse> result = agentHomeService
                .getRandomAgentCards(loginEmail);

        // then
        assertThat(result).hasSize(12);
        assertThat(result.get(0).agentSpecialityTop2()).isNotNull();
        assertThat(result.get(0).agentSpecialityTop2()).hasSizeLessThanOrEqualTo(2);
    }

    @DisplayName("등록된 행정사가 12개 미만(0개 포함)일 경우, 존재하는 데이터만큼만 반환한다.")
    @Test
    void getRandomAgentCards_LessThan12_Success() {
        // given
        int savedCount = 5;
        createAgentProfiles(savedCount);

        // when
        List<AgentCardResponse> result = agentHomeService.getRandomAgentCards(null);

        // then
        assertThat(result).hasSize(savedCount);
    }

    private void createAgentProfiles(int count) {
        com.navisa.be.common.model.entity.JobCode jobCode = jobCodeRepository.save(
                new com.navisa.be.common.model.entity.JobCode(null, "CODE", "전문분야", null, null));

        for (int i = 0; i < count; i++) {
            AgentProfile profile = agentProfileRepository.save(new AgentProfile(
                    "행정사" + i, LocalDate.now(), "url", "09:00~18:00",
                    "사무소", "서울", "강남", "경력",
                    "010-1234-1234", UUID.randomUUID(), "LIC-" + i, LocalDate.now(), "P-" + i, "M-" + i, "인사말"));
            specializedJobCodeRepository.save(new AgentSpecializedJob(profile, jobCode));
        }
    }

    @DisplayName("로그인 상태(email 존재)에서 랜덤 카드 조회 시 실제 특화 직무(JobCode) ID가 포함된다.")
    @Test
    void getRandomAgentCards_Login_WithSpeciality_Success() {
        // given
        Long actualSavedJobId = createAgentProfilesWithSpeciality(12, "비자/출입국");
        String loginEmail = "user@test.com";

        // when
        List<AgentCardResponse> result = agentHomeService.getRandomAgentCards(loginEmail);

        // then
        assertThat(result).hasSize(12);

        assertThat(result.get(0).agentSpecialityTop2())
                .isNotNull()
                .isNotEmpty()
                .contains(actualSavedJobId);

        assertThat(result).allSatisfy(response -> {
            assertThat(response.agentSpecialityTop2()).isNotNull();
            assertThat(response.agentSpecialityTop2()).hasSizeGreaterThan(0);
        });
    }

    private Long createAgentProfilesWithSpeciality(int count, String jobName) {
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "SPEC_01", jobName, null, null));

        for (int i = 0; i < count; i++) {
            AgentProfile profile = new AgentProfile(
                    "행정사" + i, LocalDate.now(), "url", "09:00~18:00",
                    "사무소", "서울", "강남", "경력",
                    "010-1234-1234", UUID.randomUUID(), "LIC-" + i, LocalDate.now(), "P-" + i, "M-" + i, "인사말");
            agentProfileRepository.save(profile);

            AgentSpecializedJob specializedJobCode = new AgentSpecializedJob(profile, jobCode);
            specializedJobCodeRepository.save(specializedJobCode);

            profile.getSpecializedJobs().add(specializedJobCode);
        }

        return jobCode.getId();
    }

    private AgentProfile saveAgentProfile(UUID userId, String name, String imageUrl) {
        AgentProfile profile = new AgentProfile(
                name, LocalDate.now(), imageUrl, "09:00~18:00",
                "내비자 사무소", "서울", "강남", "경력사항",
                "010-1234-1234", userId, "LIC-123", LocalDate.now(), "P-123", "M-123", "한마디");
        return agentProfileRepository.save(profile);
    }
}