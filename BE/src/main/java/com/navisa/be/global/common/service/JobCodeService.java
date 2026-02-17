package com.navisa.be.global.common.service;

import com.navisa.be.agent.dto.projection.JobCodeProjection;
import com.navisa.be.agent.dto.response.JobCodeListResponse;
import com.navisa.be.foreigner.model.entity.ForeignerEducation;
import com.navisa.be.foreigner.model.enums.EducationDegreeLevel;
import com.navisa.be.global.common.dto.projection.JobCodeSimilarityProjection;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.repository.JobCodeRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    @Transactional(readOnly = true)
    public List<JobCodeSimilarityProjection> findTop3SimilarJobCodes(float[] embedding) {
        return jobCodeRepository.findTop3SimilarJobCodes(embedding);
    }
}
