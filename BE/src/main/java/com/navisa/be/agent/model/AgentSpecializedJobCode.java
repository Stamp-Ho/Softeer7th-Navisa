package com.navisa.be.agent.model;

import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.common.model.entity.JobCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "agent_specialized_job_code")
@Entity
public class AgentSpecializedJobCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agent_id")
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
