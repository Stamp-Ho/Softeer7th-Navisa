package com.navisa.be.agent.model.entity;

import com.navisa.be.global.common.model.entity.BaseEntity;
import com.navisa.be.global.common.model.entity.JobCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agent_specialized_job", indexes = {
    // 1. 특정 직무 코드로 행정사를 필터링할 때 최적
    @Index(name = "idx_specialized_job_code_agent", columnList = "job_code_id, agent_id"),

    // 2. 특정 행정사의 직무들을 조회할 때 (Service 레이어용)
    @Index(name = "idx_specialized_agent_id", columnList = "agent_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentSpecializedJob extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_specialized_job_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentProfile agentProfile;

    @ManyToOne
    @JoinColumn(name = "job_code_id")
    private JobCode jobCode;

    public AgentSpecializedJob(AgentProfile agentProfile, JobCode jobCode) {
        this.agentProfile = agentProfile;
        this.jobCode = jobCode;
    }
}
