package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.JobCodeDto;
import com.navisa.be.agent.dto.response.GetJobCodeListResponse;
import com.navisa.be.common.repository.JobCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobCodeService {

    private final JobCodeRepository jobCodeRepository;

    public JobCodeService(JobCodeRepository jobCodeRepository) {
        this.jobCodeRepository = jobCodeRepository;
    }

    public GetJobCodeListResponse getJobCodeList() {
        List<JobCodeDto> dtoList = jobCodeRepository.findAllJobCodeDtos();
        return new GetJobCodeListResponse(dtoList);
    }
}
