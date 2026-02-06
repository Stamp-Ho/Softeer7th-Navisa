package com.navisa.be.recommendation.service;

import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ReviewAccumulationService {

    private final AgentSpecializedJobSummaryRepository summaryRepository;

    public ReviewAccumulationService(AgentSpecializedJobSummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    /**
     * Step 4-2: 누적 리뷰 신뢰도(zA) 갱신
     *
     * @param agentId 행정사 ID
     * @param jobCodeId 전문 분야 코드
     * @param relativeRatio 리뷰 신뢰도 비율 r_i,a
     * Native UPSERT를 활용하여 갱신 손실(Lost Update)과 중복 생성 에러를 원천 차단함
     */
    @Transactional
    public void accumulateReviewWeight(UUID agentId, Long jobCodeId, double relativeRatio) {
        // 조회, 생성, 수정을 모두 처리
        summaryRepository.upsertReliability(agentId, jobCodeId, relativeRatio);
    }
}
