package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.projection.AgentSpecializedJobWithScoreProjection;
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

    /**
     * DB 쿼리 1번: agent_specialized_job LEFT JOIN agent_specialized_job_summary
     * → 리뷰가 있는 직무는 reviewScore, 없으면 null
     * → Redis 가중치 합산 후 agentId별 Top2 선별
     */
    @Transactional(readOnly = true)
    public Map<UUID, List<Long>> getTop2SpecializedJobIdsBatch(List<UUID> agentIds) {
        if (agentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<AgentSpecializedJobWithScoreProjection> rows = agentSpecializedJobRepository
                .findAllWithReviewScoreByAgentIds(agentIds);

        // Redis 가중치 배치 조회 (Redis pipeline 1번)
        Map<String, Double> redisWeightMap = fetchRedisWeightsBatch(rows);

        // agentId별로 grouping → (reviewScore + redisWeight) 기준 정렬 → Top2 선별
        return rows.stream()
                .collect(Collectors.groupingBy(AgentSpecializedJobWithScoreProjection::getAgentId))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted((r1, r2) -> {
                                    double total1 = toScore(r1, redisWeightMap);
                                    double total2 = toScore(r2, redisWeightMap);
                                    return Double.compare(total2, total1);
                                })
                                .limit(2)
                                .map(AgentSpecializedJobWithScoreProjection::getJobCodeId)
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

    /** Projection row의 합산 점수 (RDB reviewScore + Redis weight) */
    private double toScore(AgentSpecializedJobWithScoreProjection row, Map<String, Double> redisWeightMap) {
        double reviewScore = row.getReviewScore() != null ? row.getReviewScore() : 0.0;
        double redisWeight = redisWeightMap.getOrDefault(makeRedisKey(row.getAgentId(), row.getJobCodeId()), 0.0);
        return reviewScore + redisWeight;
    }

    private Map<String, Double> fetchRedisWeightsBatch(List<AgentSpecializedJobWithScoreProjection> rows) {
        List<String> keys = rows.stream()
                .map(row -> makeRedisKey(row.getAgentId(), row.getJobCodeId()))
                .toList();

        try {
            List<Double> values = doubleRedisTemplate.opsForValue().multiGet(keys);
            if (values == null)
                return Collections.emptyMap();

            Map<String, Double> resultMap = new HashMap<>();
            for (int i = 0; i < keys.size(); i++) {
                Double val = values.get(i);
                if (val != null)
                    resultMap.put(keys.get(i), val);
            }
            return resultMap;
        } catch (Exception e) {
            log.error("[Redis MultiGet Error] Redis 장애 발생, 기본값 사용", e);
            return Collections.emptyMap();
        }
    }

    private String makeRedisKey(UUID agentId, Long jobCodeId) {
        return String.format("matching:sandbox:%s:%d", agentId, jobCodeId);
    }
}
