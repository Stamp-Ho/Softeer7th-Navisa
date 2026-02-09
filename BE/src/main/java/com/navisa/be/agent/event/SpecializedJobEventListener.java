package com.navisa.be.agent.event;

import com.navisa.be.recommendation.service.ReviewAccumulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SpecializedJobEventListener {

    private final ReviewAccumulationService reviewAccumulationService;

    /**
     * Step 4-2: 이벤트를 구독하여 서비스의 누적 로직 호출
     */
    @EventListener
    @Transactional
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
