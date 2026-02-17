package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.TopAgentByBadgeResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.repository.AgentBadgeRepository;
import com.navisa.be.agent.repository.AgentBadgeSummaryRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AgentSuggestionService {

    private final AgentProfileRepository agentProfileRepository;
    private final AgentBadgeService agentBadgeService;
    private final StorageService storageService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final AgentBadgeSummaryRepository agentBadgeSummaryRepository;
    private final AgentBadgeRepository agentBadgeRepository;

    // 랜덤 행정사 카드
    public List<AgentCardResponse> getRandomAgentCards(String email) {
        List<AgentProfile> agents = agentProfileRepository.findRandom12ValidAgents();

        if (agents.isEmpty()) {
            throw new AgentException(ResponseStatus.AGENT_CARD_NOT_FOUND);
        }

        // 로그인 여부 판단
        boolean isLoggedIn = StringUtils.hasText(email);

        return agents.stream()
                .map(agent -> {
                    return getAgentCardResponse(agent, isLoggedIn);
                })
                .toList();
    }

    private AgentCardResponse getAgentCardResponse(AgentProfile agent, boolean isLoggedIn) {
        String profileUrl = storageService.getImgUrl(ImageSize.MEDIUM, agent.getProfileObjectKey(), false);

        // 로그인 시에만 JobCode 명칭 리스트 추출
        List<Long> specialityIds = null;
        if (isLoggedIn) {
            specialityIds = agent.getSpecializedJobs().stream()
                    .map(specialized -> specialized.getJobCode().getId()) // ID 추출
                    .limit(2)
                    .toList();
        }

        List<Long> topBadges = agentBadgeService.getTop2BadgeIds(agent.getId());

        return AgentCardResponse.of(agent, profileUrl, specialityIds, topBadges);
    }

    // 배지별 행정사 추천
    public List<TopAgentByBadgeResponse> getTop10AgentsByBadge(Long badgeId) {
        List<AgentReview> reviews = agentBadgeRepository.findByBadgeId(badgeId, PageRequest.of(0, 10));

        if (reviews.isEmpty()) {
            throw new AgentException(ResponseStatus.BADGE_REVIEW_NOT_FOUND);
        }

        List<UUID> agentIds = reviews.stream().map(AgentReview::getAgentProfileId).distinct().toList();
        List<UUID> foreignerIds = reviews.stream().map(AgentReview::getForeignerProfileId).distinct().toList();

        Map<UUID, AgentProfile> agentMap = agentProfileRepository.findAllById(agentIds).stream()
                .collect(Collectors.toMap(AgentProfile::getId, Function.identity()));

        Map<UUID, ForeignerProfile> foreignerMap = foreignerProfileCrudService.findAllById(foreignerIds).stream()
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

                    return new TopAgentByBadgeResponse(
                            review.getId(),
                            foreigner != null ? maskName(foreigner.getNickname()) : "익명",
                            review.getFeedbackContent(),
                            agent != null ? agent.getId() : null,
                            agent != null ? agent.getName() : "알 수 없는 행정사",
                            agent != null ? storageService.getImgUrl(ImageSize.SMALL, agent.getProfileObjectKey(), false) : null,
                            top2BadgeIds
                    );
                })
                .toList();
    }

    // 이름 마스킹 메서드
    private String maskName(String name) {
        if (name == null || name.isEmpty()) return "";
        return name.substring(0, 1) + "*".repeat(Math.max(0, name.length() - 1));
    }
}
