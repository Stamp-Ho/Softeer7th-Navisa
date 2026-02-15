package com.navisa.be.global.common.service;

import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.JobGroup;
import com.navisa.be.global.common.repository.JobGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobGroupService {

    private final JobGroupRepository jobGroupRepository;

    @Transactional(readOnly = true)
    public List<Long> findAllJobCodeIdsByGroupNames(List<String> jobGroupNameList) {
        if (jobGroupNameList == null || jobGroupNameList.isEmpty()) {
            return List.of();
        }

        // 1. 이름 리스트로 JobGroup 엔티티들을 조회
        List<JobGroup> jobGroups = jobGroupRepository.findAllByJobGroupNameIn(jobGroupNameList);

        // 2. 각 JobGroup이 가진 JobCode 리스트를 평탄화(flatMap)하여 ID만 추출
        return jobGroups.stream()
                .flatMap(group -> group.getJobCodeList().stream())
                .map(JobCode::getId)
                .distinct() // 중복된 JobCode ID가 있을 경우 제거
                .toList();
    }
}
