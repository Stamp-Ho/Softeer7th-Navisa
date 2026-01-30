package com.navisa.be.agent.model;

import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.common.model.entity.Language;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "agent_language")
@Entity
public class AgentLanguage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
