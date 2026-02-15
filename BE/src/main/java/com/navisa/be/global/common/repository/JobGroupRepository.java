package com.navisa.be.global.common.repository;

import com.navisa.be.global.common.model.entity.JobGroup;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobGroupRepository extends JpaRepository<JobGroup, Long> {

    JobGroup findByJobGroupName(String jobGroupName);

    @EntityGraph(attributePaths = "jobCodeList")
    List<JobGroup> findAllByJobGroupNameIn(List<String> jobGroupNames);
}
