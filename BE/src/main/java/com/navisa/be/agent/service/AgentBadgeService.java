package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.BadgeResponse;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.global.web.error.BadgeException;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.repository.AgentBadgeSummaryRepository;
import com.navisa.be.agent.repository.BadgeRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentBadgeService {

    private final BadgeRepository badgeRepository;
    private final AgentBadgeSummaryRepository agentBadgeSummaryRepository;

    // 전체 배지 목록 조회
    public List<BadgeResponse> getAllBadges() {
        List<Badge> badges = badgeRepository.findAll();

        if (badges.isEmpty()) {
            throw new BadgeException(ResponseStatus.BADGE_NOT_FOUND);
        }

        return badges.stream()
                .map(BadgeResponse::from)
                .toList();
    }

    // 특정 행정사의 상위 2개 배지 Id 조회
    public List<Long> getTop2BadgeIds(UUID agentId) {
        List<AgentBadgeSummary> summaries = agentBadgeSummaryRepository.findTopKBadgeSummariesByAgentId(
                agentId, PageRequest.of(0, 2));

        return summaries.stream()
                .map(summary -> summary.getBadge().getId())
                .toList();
    }

    public List<AgentBadgeSummary> getTopKBadgeByAgentId(UUID agentId, int limit) {
        return agentBadgeSummaryRepository.findTopKBadgeSummariesByAgentId(agentId, PageRequest.of(0, limit));
    }

    public Map<UUID, List<Long>> getTop2BadgeIdsBatch(List<UUID> agentIds) {
        if (agentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<AgentBadgeSummary> summaries = agentBadgeSummaryRepository.findAllByAgentIdIn(agentIds);

        return summaries.stream()
                .collect(Collectors.groupingBy(AgentBadgeSummary::getAgentId))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparingInt(AgentBadgeSummary::getCount).reversed()
                                        .thenComparing(summary -> summary.getBadge().getId()))
                                .limit(2)
                                .map(summary -> summary.getBadge().getId())
                                .toList()));
    }
}
