package com.navisa.be.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BadgeName {

    QUICK_REPLY("빠른 답장"),
    CLEAR_SOLUTION("명확한 해결책"),
    EFFICIENT_SOLUTION("효율적인 처리"),
    THROUGH_REVIEW("꼼꼼한 검토"),
    ACCURATE_WORK("정확한 업무 처리"),
    FLEXIBLE_HANDLING("유연한 대응"),
    HIGH_EXPERTISE("높은 전문성"),
    FAST_INFORMATION("빠른 정보 전달"),
    CONTINUOUS_FEEDBACK("지속적인 피드백"),
    PUNCTUALITY("시간 약속 철저"),
    REASONABLE_COST("합리적인 비용"),
    EASY_COMMUNICATION("원활한 소통"),
    KIND_CONSULTATION("친절한 상담"),
    TRANSPARENT_COMMUNICATION("투명한 소통"),
    RELIABLE_FEEDBACK("신뢰할 수 있는 피드백");

    private final String description;
}