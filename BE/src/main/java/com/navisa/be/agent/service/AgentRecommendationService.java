package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.entity.ForeignerSimilarity;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.recommendation.calculator.FinalRecommendationCalculator;
import com.navisa.be.recommendation.calculator.ReviewBonusCalculator;
import com.navisa.be.recommendation.calculator.SpecialtyDistributionCalculator;
import com.navisa.be.global.common.model.enums.ImageSize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AgentRecommendationService {

    private final ForeignerProfileCrudService foreignerProfileCrudService;
    private final AgentProfileRepository agentProfileRepository;
    private final SpecialtyDistributionCalculator distributionCalculator;
    private final ReviewBonusCalculator reviewBonusCalculator;
    private final FinalRecommendationCalculator finalCalculator;
    private final RedisTemplate<String, Double> doubleRedisTemplate;
    private final AgentSpecializedJobService agentSpecializedJobService;
    private final AgentBadgeService agentBadgeService;
    private final StorageService storageService;

    // 맞춤 행정사 추천
    public List<AgentCardResponse> getPersonalizedAgents(String email) {
        ForeignerProfile profile = foreignerProfileCrudService.findByEmail(email);
        ForeignerSimilarity similarity = foreignerProfileCrudService.findSimilarityByForeignerId(profile.getId());

        List<AgentProfile> profiles = agentProfileRepository.findAllValidAgentProfiles();

        Map<String, Double> zaMap = prefetchRedisZaValues(profiles);

        List<AgentProfile> contentProfiles = profiles.stream()
                .map(p -> Map.entry(p, calculateScoreWithPrefetchedData(p, similarity, zaMap)))
                .sorted(Map.Entry.<AgentProfile, Double>comparingByValue().reversed())
                .limit(12)
                .map(Map.Entry::getKey)
                .toList();

        List<UUID> contentProfileIds = contentProfiles.stream().map(AgentProfile::getId).toList();
        Map<UUID, List<Long>> agentSpecialityTop2 = agentSpecializedJobService.getTop2SpecializedJobIdsBatch(contentProfileIds);
        Map<UUID, List<Long>> badgeTop2Map = agentBadgeService.getTop2BadgeIdsBatch(contentProfileIds);

        return contentProfiles.stream()
                .map(agent -> {
                    String profileUrl = storageService.getImgUrl(
                            ImageSize.SMALL,
                            agent.getProfileObjectKey(),
                            false);

                    return AgentCardResponse.of(
                            agent,
                            profileUrl,
                            agentSpecialityTop2.getOrDefault(agent.getId(), List.of()),
                            badgeTop2Map.getOrDefault(agent.getId(), List.of()));
                })
                .toList();
    }

    /**
     * 모든 대상 행정사의 직무별 가중치(za)를 Redis MGET 명령어로 일괄 조회
     */
    private Map<String, Double> prefetchRedisZaValues(List<AgentProfile> profiles) {
        List<String> keys = profiles.stream()
                .flatMap(p -> p.getSpecializedJobs().stream()
                        .map(job -> String.format("matching:sandbox:%s:%d", p.getId(), job.getJobCode().getId())))
                .distinct()
                .toList();

        if (keys.isEmpty()) return Collections.emptyMap();

        try {
            List<Double> values = doubleRedisTemplate.opsForValue().multiGet(keys);
            Map<String, Double> result = new HashMap<>();
            for (int i = 0; i < keys.size(); i++) {
                if (values != null && values.get(i) != null) {
                    result.put(keys.get(i), values.get(i));
                }
            }
            return result;
        } catch (Exception e) {
            log.error("[Redis Read Error] 추천 엔진 일괄 조회 실패: {}", e.getMessage());
            return Collections.emptyMap(); // 장애 시 0점 폴백
        }
    }

    /**
     * 사전에 조회된 Redis 데이터를 활용하여 점수를 계산
     */
    private double calculateScoreWithPrefetchedData(AgentProfile profile, ForeignerSimilarity similarity, Map<String, Double> zaMap) {
        double yn = distributionCalculator.calculateDistribution(
                profile.getSpecializedJobs() != null ? profile.getSpecializedJobs().size() : 0);

        Map<Long, Double> waMap = buildWaMap(similarity);
        Map<Long, Double> saMap = new HashMap<>();

        profile.getSpecializedJobs().forEach(job -> {
            Long jobId = job.getJobCode().getId();
            String key = String.format("matching:sandbox:%s:%d", profile.getId(), jobId);

            // 미리 조회된 Map에서 가중치 획득 (O(1))
            double za = zaMap.getOrDefault(key, 0.0);

            double gza = reviewBonusCalculator.calculateBonusFactor(za);
            double finalYn = reviewBonusCalculator.calculateFinalDistribution(yn, gza);
            double sa = profile.getActiveScore() * finalYn;
            saMap.put(jobId, sa);
        });

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
}
