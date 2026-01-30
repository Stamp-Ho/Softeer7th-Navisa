package com.navisa.be.agent.model;

import com.navisa.be.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "agent_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentReview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_review_id")
    private Long id;

    @Column(name = "agent_profile_id")
    private UUID agentProfileId;

    @Column(name = "foreigner_profile_id")
    private UUID foreignerProfileId;

    @Column(name = "feedback_content", columnDefinition = "TEXT")
    private String feedbackContent;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "similarity_list", columnDefinition = "double precision[]")
    private double[] similarityList;

    public AgentReview(UUID agentProfileId, UUID foreignerProfileId, String feedbackContent, double[] similarityList) {
        this.agentProfileId = agentProfileId;
        this.foreignerProfileId = foreignerProfileId;
        this.feedbackContent = feedbackContent;
        this.similarityList = similarityList;
    }
}