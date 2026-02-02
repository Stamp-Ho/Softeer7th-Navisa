package com.navisa.be.agent.model.entity;

import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.common.model.entity.Language;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agent_language", indexes = {
    // 1. 특정 언어로 행정사를 필터링할 때 최적
    @Index(name = "idx_agent_language_code_agent", columnList = "language_id, agent_id"),

    // 2. 특정 행정사의 언어들을 조회할 때 (Service 레이어용)
    @Index(name = "idx_language_agent_id", columnList = "agent_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgentLanguage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_language_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private AgentProfile agentProfile;

    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    public AgentLanguage(AgentProfile agentProfile, Language language) {
        this.agentProfile = agentProfile;
        this.language = language;
    }
}
