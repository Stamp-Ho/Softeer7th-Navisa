package com.navisa.be.agent.model.entity;

import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.common.model.entity.JobCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agent_specialized_job_code")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentSpecializedJobCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_specialized_job_code_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentProfile agentProfile;

    @ManyToOne
    @JoinColumn(name = "job_code_id")
    private JobCode jobCode;

    public AgentSpecializedJobCode(AgentProfile agentProfile, JobCode jobCode) {
        this.agentProfile = agentProfile;
        this.jobCode = jobCode;
    }

    public JobCode getJobCode() {
        return this.jobCode;
    }
}
