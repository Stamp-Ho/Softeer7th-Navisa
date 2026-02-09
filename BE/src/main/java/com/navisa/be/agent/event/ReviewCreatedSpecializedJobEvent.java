package com.navisa.be.agent.event;

import java.util.List;
import java.util.UUID;

/**
 * 행정사의 특화 직무 신뢰도 업데이트를 위한 이벤트
 * @param agentId 행정사 ID
 * @param specializedJobIds 상위 3개 직무 ID 리스트
 * @param relativeRatios 각 직무별 계산된 상대 비율 r_i,a 리스트
 */
public record ReviewCreatedSpecializedJobEvent(
        UUID agentId,
        List<Long> specializedJobIds,
        List<Double> relativeRatios
) {
}
