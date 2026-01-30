package com.navisa.be.agent.model;

import com.navisa.be.common.model.entity.Badge;
import com.navisa.be.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agent_badge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AgentBadge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_badge_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_review_id", nullable = false)
    private AgentReview agentReview;

    public AgentBadge(Badge badge, AgentReview agentReview) {
        this.badge = badge;
        this.agentReview = agentReview;
    }
}
