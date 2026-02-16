package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.recommendation.calculator.FinalRecommendationCalculator;
import com.navisa.be.recommendation.calculator.ReviewBonusCalculator;
import com.navisa.be.recommendation.calculator.SpecialtyDistributionCalculator;
import com.navisa.be.global.common.model.enums.ImageSize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AgentRecommendationService {

    private final ForeignerQueryService foreignerQueryService;
    private final AgentProfileRepository agentProfileRepository;
    private final SpecialtyDistributionCalculator distributionCalculator;
    private final ReviewBonusCalculator reviewBonusCalculator;
    private final FinalRecommendationCalculator finalCalculator;
    private final AgentSpecializedJobSummaryRepository summaryRepository;
    private final AgentSpecializedJobService agentSpecializedJobService;
    private final AgentBadgeService agentBadgeService;
    private final StorageService storageService;

    // 맞춤 행정사 추천
    public List<AgentCardResponse> getPersonalizedAgents(String email) {

        UUID foreignerId = foreignerQueryService.getForeignerIdByEmail(email);

        ForeignerSimilarity similarity = foreignerQueryService.findSimilarityByForeignerId(foreignerId);

        List<AgentProfile> profiles = agentProfileRepository.findAllValidAgentProfiles();

        List<UUID> agentIds = profiles.stream().map(AgentProfile::getId).toList();
        List<AgentSpecializedJobSummary> allSummaries = summaryRepository.findAllByAgentIdIn(agentIds);

        Map<UUID, List<AgentSpecializedJobSummary>> summaryGroupByAgent = allSummaries.stream()
                .collect(Collectors.groupingBy(AgentSpecializedJobSummary::getAgentId));

        return profiles.stream()
                .sorted((p1, p2) -> {
                    double score1 = calculatePersonalizedScore(p1, similarity, summaryGroupByAgent.getOrDefault(p1.getId(), List.of()));
                    double score2 = calculatePersonalizedScore(p2, similarity, summaryGroupByAgent.getOrDefault(p2.getId(), List.of()));
                    return Double.compare(score2, score1);
                })
                .limit(12)
                .map(agent -> toAgentCardResponse(agent))
                .toList();
    }

    private double calculatePersonalizedScore(AgentProfile profile,
                                              ForeignerSimilarity similarity,
                                              List<AgentSpecializedJobSummary> summaries) {
        // [Step 3] 기본 분배 점수 y(n)
        double yn = distributionCalculator.calculateDistribution(profile.getSpecializedJobs().size());

        // [Step 4] 유저 관심도 waMap 구성
        Map<Long, Double> waMap = buildWaMap(similarity);

        // [Step 5] 분야별 saMap 구성
        Map<Long, Double> saMap = new HashMap<>();
        for (AgentSpecializedJobSummary summary : summaries) {
            Long jobId = summary.getJobCode().getId();
            double za = summary.getAccumulatedReviewReliability();

            double gza = reviewBonusCalculator.calculateBonusFactor(za);
            double finalYn = reviewBonusCalculator.calculateFinalDistribution(yn, gza);

            // sa = T * y(n, za)
            double sa = profile.getActiveScore() * finalYn;
            saMap.put(jobId, sa);
        }

        // [Step 6] G = Σ(wa * sa)
        return finalCalculator.calculateFinalGradeByLongId(saMap, waMap);
    }

    private Map<Long, Double> buildWaMap(ForeignerSimilarity similarity) {
        Map<Long, Double> waMap = new HashMap<>();
        long[] jobIds = similarity.getJobCodeIdList();
        double[] weights = similarity.getSimilarityList();

        if (jobIds != null && weights != null && jobIds.length == weights.length) {
            for (int i = 0; i < jobIds.length; i++) {
                waMap.put(jobIds[i], weights[i]);
            }
        }
        return waMap;
    }

    private AgentCardResponse toAgentCardResponse(AgentProfile agent) {
        String profileUrl = storageService.getImgUrl(ImageSize.SMALL, agent.getProfileObjectKey(), false);
        return AgentCardResponse.of(
                agent,
                profileUrl,
                agentSpecializedJobService.getTop2SpecializedJobIds(agent.getId()),
                agentBadgeService.getTop2BadgeIds(agent.getId())
        );
    }
}
