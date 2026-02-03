package com.navisa.be.info.model.entity;

import com.navisa.be.common.model.entity.JobCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "job_group")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class JobGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_group_id")
    private Long id;

    @Column(name = "job_group_name", unique = true)
    private String jobGroupName;

    @OneToMany
    @JoinColumn(name = "job_group_id")
    private List<JobCode> jobCodeList;
}
