package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.projection.AgentSpecializedJobWithCountProjection;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.service.JobCodeService;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AgentSpecializedJobService {

    private final AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;
    private final AgentSpecializedJobRepository agentSpecializedJobRepository;
    private final JobCodeService jobCodeService;
    private final RedisTemplate<String, Double> doubleRedisTemplate;

    // 특정 행정사의 상위 2개 직종코드 Id 조회
    @Transactional(readOnly = true)
    public List<Long> getTop2SpecializedJobIds(UUID agentId) {
        List<AgentSpecializedJobSummary> summaries = agentSpecializedJobSummaryRepository.findTop2SummaryByAgentId(
                agentId, PageRequest.of(0, 2));

        return summaries.stream()
                .map(summary -> summary.getJobCode().getId())
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<UUID, List<Long>> getTop2SpecializedJobIdsBatch(List<UUID> agentIds) {
        if (agentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<AgentSpecializedJobWithCountProjection> rows = agentSpecializedJobRepository
                .findAllWithCountByAgentIds(agentIds);

        // agentId별로 grouping → count 기준 정렬 → Top2 선별
        return rows.stream()
                .collect(Collectors.groupingBy(AgentSpecializedJobWithCountProjection::agentId))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted((r1, r2) -> Integer.compare(
                                        r2.count() != null ? r2.count() : 0,
                                        r1.count() != null ? r1.count() : 0))
                                .limit(2)
                                .map(AgentSpecializedJobWithCountProjection::jobCodeId)
                                .toList()));
    }

    public void save(List<Long> jobCodeIds, AgentProfile agentProfile) {
        jobCodeIds = jobCodeIds.stream().distinct().toList();
        checkAllJobCodeExists(jobCodeIds);

        // 모두 조회해서 연관관계를 저장
        List<JobCode> jobCodes = jobCodeService.findAllById(jobCodeIds);
        List<AgentSpecializedJob> specializedJobCodes = jobCodes.stream()
                .map(jobCode -> new AgentSpecializedJob(agentProfile, jobCode))
                .toList();
        List<AgentSpecializedJob> savedCodes = agentSpecializedJobRepository.saveAll(specializedJobCodes);

        agentProfile.addSpecializedJobCodes(savedCodes);
    }

    private void checkAllJobCodeExists(List<Long> jobCodeIds) {
        long requestCount = jobCodeIds.size();
        long foundCount = jobCodeService.countByIdIn(jobCodeIds);
        if (requestCount != foundCount) {
            throw new AgentException(ResponseStatus.INVALID_JOB_CODE);
        }
    }

    public void deleteAllByAgentProfile(AgentProfile agentProfile) {
        agentSpecializedJobRepository.deleteAllByAgentProfile(agentProfile);
        agentProfile.getSpecializedJobs().clear();
    }

    /**
     * Redis 실시간 가중치(za)와 DB 누적 신뢰도(accumulatedReviewReliability)를
     * 모두 조회하여 합산된 가중치 Map을 반환
     */
    @Transactional(readOnly = true)
    public Map<String, Double> getCombinedWeightMap(List<UUID> agentIds, List<Long> targetJobIds) {
        if (agentIds.isEmpty() || targetJobIds.isEmpty()) return Collections.emptyMap();

        List<AgentSpecializedJobSummary> summaries = agentSpecializedJobSummaryRepository.findAllByAgentIdIn(agentIds);

        List<String> keys = new ArrayList<>();
        for (UUID agentId : agentIds) {
            for (Long jobId : targetJobIds) {
                keys.add(String.format("matching:sandbox:%s:%d", agentId, jobId));
            }
        }

        try {
            List<Double> redisValues = doubleRedisTemplate.opsForValue().multiGet(keys);
            Map<String, Double> resultMap = new HashMap<>();

            if (redisValues != null) {
                for (int i = 0; i < keys.size(); i++) {
                    Double val = redisValues.get(i);
                    if (val != null) resultMap.put(keys.get(i), val);
                }
            }

            for (AgentSpecializedJobSummary s : summaries) {
                String key = String.format("matching:sandbox:%s:%d", s.getAgentId(), s.getJobCode().getId());
                double redisZa = resultMap.getOrDefault(key, 0.0);
                double dbAcc = s.getAccumulatedReviewReliability();

                // 실시간 가중치 + 누적 신뢰도 합산값 저장
                resultMap.put(key, redisZa + dbAcc);
            }

            return resultMap;
        } catch (Exception e) {
            log.error("[Weight Fetch Error] 가중치 조회 중 오류 발생", e);
            return Collections.emptyMap();
        }
    }

    private String makeRedisKey(AgentSpecializedJobSummary summary) {
        return String.format("matching:sandbox:%s:%d", summary.getAgentId(), summary.getJobCode().getId());
    }
}
