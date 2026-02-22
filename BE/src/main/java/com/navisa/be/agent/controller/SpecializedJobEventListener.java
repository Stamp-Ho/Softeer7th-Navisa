package com.navisa.be.agent.controller;

import com.navisa.be.agent.event.ReviewCreatedSpecializedJobEvent;
import com.navisa.be.recommendation.service.ReviewAccumulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SpecializedJobEventListener {

    private final ReviewAccumulationService reviewAccumulationService;

    /**
     * Step 4-2: 이벤트를 구독하여 서비스의 누적 로직 호출
     */
    @Async("reliabilityAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReviewCreatedSpecializedJobEvent(ReviewCreatedSpecializedJobEvent event) {
        int size = event.specializedJobIds().size();

        for (int i = 0; i < size; i++) {
            Long jobCodeId = event.specializedJobIds().get(i);
            Double relativeRatio = event.relativeRatios().get(i);
            reviewAccumulationService.accumulateReviewWeight(
                    event.agentId(),
                    jobCodeId,
                    relativeRatio
            );
        }
    }
}
