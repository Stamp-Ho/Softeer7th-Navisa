package com.navisa.be.agent.repository;

import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class AgentReviewRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private AgentReviewRepository agentReviewRepository;

    @DisplayName("최신순으로 정렬된 리뷰를 최대 4개까지만 조회한다.")
    @Test
    void findTop4ByOrderByCreatedAtDesc() {
        // given
        UUID agentId = UUID.randomUUID();
        UUID foreignerId = UUID.randomUUID();
        double[] similarities = {0.8, 0.9};

        // 5개의 리뷰를 생성하여 저장 (나중에 들어온 것이 최신)
        for (int i = 1; i <= 5; i++) {
            agentReviewRepository.save(new AgentReview(
                    agentId,
                    foreignerId,
                    "리뷰 내용 " + i,
                    similarities
            ));
        }

        // when
        List<AgentReview> result = agentReviewRepository.findTop4ValidFeedbacks(PageRequest.of(0, 4));

        // then
        assertThat(result).hasSize(4); // 5개를 넣었지만 4개만 나와야 함
        assertThat(result.get(0).getFeedbackContent()).isEqualTo("리뷰 내용 5"); // 최신순(DESC) 확인
        assertThat(result.get(3).getFeedbackContent()).isEqualTo("리뷰 내용 2"); // 4번째 데이터 확인
    }

    @DisplayName("리뷰가 하나도 없을 경우 빈 리스트를 반환한다.")
    @Test
    void findTop4WhenEmpty() {
        // given & when
        List<AgentReview> result = agentReviewRepository.findTop4ValidFeedbacks(PageRequest.of(0, 4));

        // then
        assertThat(result).isEmpty();
        assertThat(result).isNotNull();
    }
}