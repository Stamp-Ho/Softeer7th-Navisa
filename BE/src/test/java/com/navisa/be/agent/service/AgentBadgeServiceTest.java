package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.HomeAgentBadgeResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.*;
import com.navisa.be.agent.repository.*;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.agent.service.AgentBadgeService;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
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
class AgentBadgeServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentBadgeService agentBadgeService;

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

    @DisplayName("배지별 행정사 추천 시 리뷰 정보와 함께 행정사 정보, 외국인 닉네임 마스킹, 상위 2개 배지가 정확히 매핑된다.")
    @Test
    void getTop10AgentsByBadge_Success() {
        Badge kindBadge = badgeRepository.save(new Badge(BadgeName.KIND_CONSULTATION));
        Badge expertBadge = badgeRepository.save(new Badge(BadgeName.HIGH_EXPERTISE));
        Badge quickBadge = badgeRepository.save(new Badge(BadgeName.QUICK_REPLY));

        AgentProfile profile = new AgentProfile(
                "김행정", LocalDate.now(), "https://image.com", "09:00~18:00",
                "내비자 사무소", "서울", "강남", "행정 전문",
                UUID.randomUUID(), "LIC-123", LocalDate.now(), "P-123", "M-123", "안녕하세요");
        AgentProfile savedProfile = agentProfileRepository.save(profile);
        UUID agentId = savedProfile.getId();

        ForeignerProfile foreigner = new ForeignerProfile(UUID.randomUUID(), ForeignerSearchStatus.REQUESTING);
        ReflectionTestUtils.setField(foreigner, "nickname", "JohnDoe");
        ForeignerProfile savedForeigner = foreignerProfileRepository.save(foreigner);
        UUID foreignerId = savedForeigner.getId();

        AgentReview review = agentReviewRepository.save(new AgentReview(
                agentId, foreignerId, "상담이 매우 친절합니다!", new double[] { 0.1, 0.2 }));

        agentBadgeRepository.save(new AgentBadge(kindBadge, review));

        // 배지 통계 데이터 저장
        saveSummaryWithCount(agentId, quickBadge, 100);
        saveSummaryWithCount(agentId, expertBadge, 50);
        saveSummaryWithCount(agentId, kindBadge, 10);

        List<HomeAgentBadgeResponse> result = agentBadgeService.getTop10AgentsByBadge(kindBadge.getId());

        assertThat(result).isNotEmpty();
        HomeAgentBadgeResponse response = result.get(0);

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
        assertThatThrownBy(() -> agentBadgeService.getTop10AgentsByBadge(invalidBadgeId))
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