package com.navisa.be.agent.model;

import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.common.model.entity.Language;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agent_language")
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
