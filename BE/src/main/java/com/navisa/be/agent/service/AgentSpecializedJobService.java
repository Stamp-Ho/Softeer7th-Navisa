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
}
