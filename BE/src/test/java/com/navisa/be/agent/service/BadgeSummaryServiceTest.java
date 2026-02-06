package com.navisa.be.agent.service;

import com.navisa.be.agent.event.ReviewCreatedBadgeEvent;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.agent.repository.AgentBadgeSummaryRepository;
import com.navisa.be.agent.repository.BadgeRepository;
import com.navisa.be.agent.service.BadgeSummaryService;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class BadgeSummaryServiceTest extends IntegrationTestSupport {

    @Autowired
    private BadgeSummaryService badgeSummaryService;

    @Autowired
    private AgentBadgeSummaryRepository summaryRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Test
    @DisplayName("리뷰 생성 이벤트 수신 시 배지 요약 정보가 신규 생성되거나 카운트가 증가해야 한다")
    void updateBadgeSummary_Success() {
        // given
        UUID agentId = UUID.randomUUID();

        Badge badge1 = badgeRepository.save(new Badge(BadgeName.KIND_CONSULTATION));
        Badge badge2 = badgeRepository.save(new Badge(BadgeName.HIGH_EXPERTISE));
        List<Long> badgeIds = List.of(badge1.getId(), badge2.getId());

        summaryRepository.save(new AgentBadgeSummary(agentId, badge1));

        ReviewCreatedBadgeEvent event = new ReviewCreatedBadgeEvent(agentId, badgeIds);

        // when
        badgeSummaryService.updateBadgeSummary(event);

        // then
        AgentBadgeSummary summary1 = summaryRepository.findByAgentIdAndBadge(agentId, badge1).orElseThrow();
        assertThat(summary1.getCount()).isEqualTo(2);

        AgentBadgeSummary summary2 = summaryRepository.findByAgentIdAndBadge(agentId, badge2).orElseThrow();
        assertThat(summary2.getCount()).isEqualTo(1);
    }
}
