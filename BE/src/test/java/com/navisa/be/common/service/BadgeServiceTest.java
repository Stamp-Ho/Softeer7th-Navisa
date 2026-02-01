package com.navisa.be.common.service;

import com.navisa.be.agent.service.AgentBadgeService;
import com.navisa.be.agent.dto.response.BadgeResponse;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.agent.repository.BadgeRepository;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
public class BadgeServiceTest extends IntegrationTestSupport {

    @Autowired
    private AgentBadgeService badgeService;

    @Autowired
    private BadgeRepository badgeRepository;

    @Test
    @DisplayName("시스템에 등록된 전체 배지 목록을 조회한다.")
    void getAllBadges() {
        // given
        badgeRepository.save(new Badge(BadgeName.QUICK_REPLY));
        badgeRepository.save(new Badge(BadgeName.CLEAR_SOLUTION));
        badgeRepository.save(new Badge(BadgeName.EFFICIENT_SOLUTION));

        // when
        List<BadgeResponse> result = badgeService.getAllBadges();

        // then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting("badgeName")
                .containsExactlyInAnyOrder("QUICK_REPLY", "CLEAR_SOLUTION", "EFFICIENT_SOLUTION");

        // ID 값이 할당되었는지 확인
        assertThat(result.get(0).badgeId()).isNotNull();
    }

    @Test
    @DisplayName("배지 목록이 비어있을 경우 404 예외가 발생한다.")
    void getAllBadgesWhenEmpty() {
        // given
        badgeRepository.deleteAllInBatch();

        // when // then
        assertThatThrownBy(() -> badgeService.getAllBadges())
                .isInstanceOf(BaseException.class);
    }
}
