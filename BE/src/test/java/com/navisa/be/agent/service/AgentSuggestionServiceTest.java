package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.TopAgentByBadgeResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.*;
import com.navisa.be.agent.repository.*;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AgentSuggestionServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentSuggestionService agentSuggestionService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private AgentReviewRepository agentReviewRepository;

    @Autowired
    private AgentBadgeRepository agentBadgeRepository;

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private AgentBadgeSummaryRepository agentBadgeSummaryRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private AgentSpecializedJobRepository specializedJobCodeRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("비로그인 상태(email=null)에서 랜덤 카드 조회 시 전문분야는 null로 반환된다.")
    @Test
    void getRandomAgentCards_Guest_Success() {
        // given
        createAgentProfiles(12);
        String emptyEmail = "";

        // when
        List<AgentCardResponse> result = agentSuggestionService.getRandomAgentCards(emptyEmail);

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
        List<AgentCardResponse> result = agentSuggestionService
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
        List<AgentCardResponse> result = agentSuggestionService.getRandomAgentCards(null);

        // then
        assertThat(result).hasSize(savedCount);
    }

    private void createAgentProfiles(int count) {
        JobCode jobCode = jobCodeRepository.save(
                new JobCode(null, "CODE", "전문분야", null, null));

        for (int i = 0; i < count; i++) {
            User user = userRepository.save(new User(
                    "agent" + i + "@test.com",
                    null,
                    UserType.VALID_AGENT,
                    LoginType.GOOGLE,
                    true
            ));

            AgentProfile profile = new AgentProfile(
                    "행정사" + i,
                    LocalDate.now(),
                    "profile-key-" + i,
                    "09:00~18:00",
                    "내비자 사무소",
                    "서울시",
                    "강남구",
                    "추가 이력",
                    "010-1234-5678",
                    user.getId(),
                    "LIC-123",
                    LocalDate.now(),
                    "P-123",
                    "M-123",
                    "인사말"
            );
            agentProfileRepository.save(profile);

            AgentSpecializedJob specializedJob = new AgentSpecializedJob(profile, jobCode);
            specializedJobCodeRepository.save(specializedJob);
            profile.getSpecializedJobs().add(specializedJob);
        }
    }

    @DisplayName("로그인 상태(email 존재)에서 랜덤 카드 조회 시 실제 특화 직무(JobCode) ID가 포함된다.")
    @Test
    void getRandomAgentCards_Login_WithSpeciality_Success() {
        // given
        Long actualSavedJobId = createAgentProfilesWithSpeciality(12, "비자/출입국");
        String loginEmail = "user@test.com";

        // when
        List<AgentCardResponse> result = agentSuggestionService.getRandomAgentCards(loginEmail);

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
            User user = userRepository.save(new User(
                    "special" + i + "@test.com",
                    null,
                    UserType.VALID_AGENT,
                    LoginType.GOOGLE,
                    true
            ));

            AgentProfile profile = new AgentProfile(
                    "행정사" + i, LocalDate.now(), "url", "09:00~18:00",
                    "사무소", "서울", "강남", "경력",
                    "010-1234-1234",
                    user.getId(),
                    "LIC-" + i, LocalDate.now(), "P-" + i, "M-" + i, "인사말");
            agentProfileRepository.save(profile);

            AgentSpecializedJob specializedJobCode = new AgentSpecializedJob(profile, jobCode);
            specializedJobCodeRepository.save(specializedJobCode);

            profile.getSpecializedJobs().add(specializedJobCode);
        }

        return jobCode.getId();
    }

    private AgentProfile saveAgentProfile(UUID userId, String name, String imageUrl) {
        User user = userRepository.save(new User(
                "test-" + UUID.randomUUID() + "@test.com",
                null,
                UserType.VALID_AGENT,
                LoginType.GOOGLE,
                true
        ));

        AgentProfile profile = new AgentProfile(
                name, LocalDate.now(), imageUrl, "09:00~18:00",
                "내비자 사무소", "서울", "강남", "경력사항",
                "010-1234-1234",
                user.getId(),
                "LIC-123", LocalDate.now(), "P-123", "M-123", "한마디");
        return agentProfileRepository.save(profile);
    }

    @DisplayName("배지별 행정사 추천 시 리뷰 정보와 함께 행정사 정보, 외국인 닉네임 마스킹, 상위 2개 배지가 정확히 매핑된다.")
    @Test
    void getTop10AgentsByBadge_Success() {
        Badge kindBadge = badgeRepository.save(new Badge(BadgeName.KIND_CONSULTATION));
        Badge expertBadge = badgeRepository.save(new Badge(BadgeName.HIGH_EXPERTISE));
        Badge quickBadge = badgeRepository.save(new Badge(BadgeName.QUICK_REPLY));

        AgentProfile profile = new AgentProfile(
                "김행정", LocalDate.now(), "https://image.com", "09:00~18:00",
                "내비자 사무소", "서울", "강남", "행정 전문",
                "010-1234-1234", UUID.randomUUID(), "LIC-123", LocalDate.now(), "P-123", "M-123", "안녕하세요");
        AgentProfile savedProfile = agentProfileRepository.save(profile);
        UUID agentId = savedProfile.getId();

        ForeignerProfile foreigner = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.REQUESTING);
        ReflectionTestUtils.setField(foreigner, "nickname", "JohnDoe");
        ForeignerProfile savedForeigner = foreignerProfileRepository.save(foreigner);
        UUID foreignerId = savedForeigner.getId();

        AgentReview review = agentReviewRepository.save(new AgentReview(
                agentId, foreignerId, 1L, "상담이 매우 친절합니다!", new double[] { 0.1, 0.2 }));

        agentBadgeRepository.save(new AgentBadge(kindBadge, review));

        // 배지 통계 데이터 저장
        saveSummaryWithCount(agentId, quickBadge, 100);
        saveSummaryWithCount(agentId, expertBadge, 50);
        saveSummaryWithCount(agentId, kindBadge, 10);

        List<TopAgentByBadgeResponse> result = agentSuggestionService.getTop10AgentsByBadge(kindBadge.getId());

        assertThat(result).isNotEmpty();
        TopAgentByBadgeResponse response = result.get(0);

        assertThat(response.agentName()).isEqualTo("김행정");

        // 마스킹 로직 확인
        assertThat(response.reviewerInitial()).isEqualTo("J******");

        assertThat(response.reviewContent()).isEqualTo("상담이 매우 친절합니다!");

        assertThat(response.badgeTop2()).hasSize(2);
        assertThat(response.badgeTop2().get(0)).isEqualTo(quickBadge.getId());
        assertThat(response.badgeTop2().get(1)).isEqualTo(expertBadge.getId());
    }

    @DisplayName("리뷰가 없는 배지 ID 조회 시 BADGE_REVIEW_NOT_FOUND 예외가 발생한다.")
    @Test
    void getTop10AgentsByBadge_NotFound() {
        // given
        Long invalidBadgeId = 9999L;

        // when & then
        assertThatThrownBy(() -> agentSuggestionService.getTop10AgentsByBadge(invalidBadgeId))
                .isInstanceOf(AgentException.class)
                .hasMessageContaining(ResponseStatus.BADGE_REVIEW_NOT_FOUND.getMessage());
    }

    private void saveSummaryWithCount(UUID agentId, Badge badge, int count) {
        AgentBadgeSummary summary = new AgentBadgeSummary(agentId, badge);
        // 생성자에서 count가 1로 세팅되므로 Reflection으로 테스트 값 강제 주입
        ReflectionTestUtils.setField(summary, "count", count);
        agentBadgeSummaryRepository.save(summary);
    }
}