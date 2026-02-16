package com.navisa.be.agent.service;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AgentSpecializedJobService {

    private final AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;
    private final AgentSpecializedJobRepository agentSpecializedJobRepository;
    private final JobCodeService jobCodeService;

    // 특정 행정사의 상위 2개 직종코드 Id 조회
    @Transactional(readOnly = true)
    public List<Long> getTop2SpecializedJobIds(UUID agentId) {
        List<AgentSpecializedJobSummary> summaries = agentSpecializedJobSummaryRepository.findTop2SummaryByAgentId(
                agentId, PageRequest.of(0, 2)
        );

        return summaries.stream()
                .map(summary -> summary.getJobCode().getId())
                .toList();
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
        long requestCount = jobCodeIds.stream().count();
        long foundCount = jobCodeService.countByIdIn(jobCodeIds);
        if (requestCount != foundCount) {
            throw new AgentException(ResponseStatus.INVALID_JOB_CODE);
        }
    }
}
