package com.navisa.be.agent.model.entity;

import com.navisa.be.common.model.entity.JobCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(
    name = "agent_specialized_job_summary",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_agent_jobcode_combination",
            columnNames = {"agent_id", "job_code_id"}
        )
    }
)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentSpecializedJobSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_specialized_job_summary_id")
    @Getter
    private Long id;

    @Column(name = "agent_id", nullable = false)
    private UUID agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_code_id", nullable = false)
    @Getter
    private JobCode jobCode;

    @Column(name = "count", nullable = false)
    private int count;

    @Column(name = "accumulated_review_reliability", nullable = false)
    @Getter
    private double accumulatedReviewReliability;  // 누적 리뷰 신뢰도

    public AgentSpecializedJobSummary(UUID agentId, JobCode jobCode) {
        this.agentId = agentId;
        this.jobCode = jobCode;
        this.count = 1;
        this.accumulatedReviewReliability = 0.0; // 초기값
    }

    public void incrementCount() {
        this.count++;
    }

    public void addAccumulatedReviewReliability(double reviewWeight) {
        this.accumulatedReviewReliability += reviewWeight;
    }
}
