package com.navisa.be.global.common.service;

import com.navisa.be.agent.dto.projection.JobCodeProjection;
import com.navisa.be.agent.dto.response.JobCodeListResponse;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.repository.JobCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobCodeService {

    private final JobCodeRepository jobCodeRepository;

    public JobCodeService(JobCodeRepository jobCodeRepository) {
        this.jobCodeRepository = jobCodeRepository;
    }

    @Transactional(readOnly = true)
    public JobCodeListResponse getJobCodeList() {
        List<JobCodeProjection> dtoList = jobCodeRepository.findAllJobCodeDtos();
        return new JobCodeListResponse(dtoList);
    }

    public List<JobCode> findAllById(List<Long> jobCodeIds) {
        return jobCodeRepository.findAllById(jobCodeIds);
    }

    public long countByIdIn(List<Long> jobCodeIds) {
        return jobCodeRepository.countByIdIn(jobCodeIds);
    }
}
