package com.navisa.be.agent.model.entity;

import com.navisa.be.global.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "agent_badge_summary",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_agent_badge_combination",
                        columnNames = {"agent_id", "badge_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentBadgeSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_badge_summary_id")
    private Long id;

    @Column(name = "agent_id", nullable = false)
    private UUID agentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @Column(name = "count", nullable = false)
    private Integer count;

    public AgentBadgeSummary(UUID agentId, Badge badge) {
        this.agentId = agentId;
        this.badge = badge;
        this.count = 1;
    }

    public void incrementCount() {
        this.count++;
    }
}
