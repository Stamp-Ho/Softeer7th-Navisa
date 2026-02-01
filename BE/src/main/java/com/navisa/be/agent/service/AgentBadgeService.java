package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.BadgeResponse;
import com.navisa.be.agent.dto.response.HomeAgentBadgeResponse;
import com.navisa.be.agent.exception.AgentHomeException;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.repository.*;
import com.navisa.be.common.exception.BadgeException;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentBadgeService {

    private final BadgeRepository badgeRepository;
    private final ForeignerProfileRepository foreignerProfileRepository;
    private final AgentBadgeSummaryRepository agentBadgeSummaryRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final AgentBadgeRepository agentBadgeRepository;

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

    // 배지별 행정사 추천
    public List<HomeAgentBadgeResponse> getTop10AgentsByBadge(Long badgeId) {
        List<AgentReview> reviews = agentBadgeRepository.findByBadgeId(badgeId, PageRequest.of(0, 10));

        if (reviews.isEmpty()) {
            throw new AgentHomeException(ResponseStatus.BADGE_REVIEW_NOT_FOUND);
        }

        List<UUID> agentIds = reviews.stream().map(AgentReview::getAgentProfileId).distinct().toList();
        List<UUID> foreignerIds = reviews.stream().map(AgentReview::getForeignerProfileId).distinct().toList();

        Map<UUID, AgentProfile> agentMap = agentProfileRepository.findAllById(agentIds).stream()
                .collect(Collectors.toMap(AgentProfile::getId, Function.identity()));
        Map<UUID, ForeignerProfile> foreignerMap = foreignerProfileRepository.findAllById(foreignerIds).stream()
                .collect(Collectors.toMap(ForeignerProfile::getId, Function.identity()));

        // 모든 행정사의 배지 요약 정보 Batch 조회
        List<AgentBadgeSummary> allSummaries = agentBadgeSummaryRepository.findAllByAgentIdIn(agentIds);

        Map<UUID, List<Long>> top2BadgesMap = allSummaries.stream()
                .collect(Collectors.groupingBy(
                        AgentBadgeSummary::getAgentId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(AgentBadgeSummary::getCount).reversed())
                                        .limit(2)
                                        .map(summary -> summary.getBadge().getId())
                                        .toList()
                        )
                ));

        return reviews.stream()
                .map(review -> {
                    AgentProfile agent = agentMap.get(review.getAgentProfileId());
                    ForeignerProfile foreigner = foreignerMap.get(review.getForeignerProfileId());
                    List<Long> top2BadgeIds = top2BadgesMap.getOrDefault(review.getAgentProfileId(), List.of());

                    return new HomeAgentBadgeResponse(
                            review.getId(),
                            maskName(foreigner != null ? foreigner.getNickname() : "익명"),
                            review.getFeedbackContent(),
                            agent != null ? agent.getId() : null,
                            agent != null ? agent.getName() : "알 수 없는 행정사",
                            agent != null ? agent.getProfileImageUrl() : null,
                            top2BadgeIds
                    );
                })
                .toList();
    }

    // 특정 행정사의 상위 2개 배지 Id 조회
    public List<Long> getTop2BadgeIds(UUID agentId) {
        List<AgentBadgeSummary> summaries = agentBadgeSummaryRepository.findTop2SummaryByAgentId(
                agentId, PageRequest.of(0, 2)
        );

        return summaries.stream()
                .map(summary -> summary.getBadge().getId())
                .toList();
    }

    // 이름 마스킹 메서드
    private String maskName(String name) {
        if (name == null || name.isEmpty()) return "";
        return name.substring(0, 1) + "*".repeat(Math.max(0, name.length() - 1));
    }
}
