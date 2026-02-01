package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.FeedbackResponse;
import com.navisa.be.agent.exception.AgentHomeException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentReview;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentHomeService {

    private final AgentReviewRepository agentReviewRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final AgentBadgeService agentBadgeService;

    // 행정사 후기 사례 최신순 4개 조회
    public List<FeedbackResponse> getLatestFeedbacks() {
        List<AgentReview> reviews = agentReviewRepository.findTop4ValidFeedbacks(PageRequest.of(0, 4));

        if (reviews.isEmpty()) {
            throw new AgentHomeException(ResponseStatus.AGENT_REVIEW_NOT_FOUND);
        }

        List<UUID> profileIds = reviews.stream()
                .map(AgentReview::getAgentProfileId)
                .distinct()
                .toList();

        Map<UUID, AgentProfile> profileMap = agentProfileRepository.findAllById(profileIds)
                .stream()
                .collect(Collectors.toMap(AgentProfile::getId, Function.identity()));

        return reviews.stream()
                .map(review -> {
                    AgentProfile profile = profileMap.get(review.getAgentProfileId());

                    if (profile == null) {
                        throw new AgentHomeException(ResponseStatus.AGENT_NOT_FOUND);
                    }

                    return new FeedbackResponse(
                            review.getId(),
                            review.getFeedbackContent(),
                            profile.getId(),
                            profile.getName(),
                            profile.getProfileImageUrl()
                    );
                })
                .toList();
    }

    // 랜덤 행정사 카드
    @Transactional(readOnly = true)
    public List<AgentCardResponse> getRandomAgentCards(String email) {
        List<AgentProfile> agents = agentProfileRepository.findRandom12();

        if (agents.isEmpty()) {
            throw new AgentHomeException(ResponseStatus.AGENT_CARD_NOT_FOUND);
        }

        // 로그인 여부 판단
        boolean isLoggedIn = StringUtils.hasText(email);

        return agents.stream()
                .map(agent -> {
                    // 로그인 시에만 JobCode 명칭 리스트 추출
                    List<Long> specialityIds = null;
                    if (isLoggedIn) {
                        specialityIds = agent.getSpecializedJobCodes().stream()
                                .map(specialized -> specialized.getJobCode().getId()) // ID 추출
                                .limit(2)
                                .toList();
                    }

                    List<Long> topBadges = agentBadgeService.getTop2BadgeIds(agent.getId());

                    return AgentCardResponse.of(agent, specialityIds, topBadges);
                })
                .toList();
    }
}
