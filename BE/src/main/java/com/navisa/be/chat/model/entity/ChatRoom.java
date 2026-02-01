package com.navisa.be.chat.model.entity;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.common.model.entity.BaseEntity;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foreigner_id", nullable = false)
    private ForeignerProfile foreignerProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentProfile agentProfile;

    @Column(name = "is_chatting", nullable = false)
    private Boolean isChatting = true;

    @Column(name = "last_chatted_at", nullable = false)
    private ZonedDateTime lastChattedAt;

    public ChatRoom(ForeignerProfile foreignerProfile, AgentProfile agentProfile, ZonedDateTime lastChattedAt) {
        this.foreignerProfile = foreignerProfile;
        this.agentProfile = agentProfile;
        this.lastChattedAt = lastChattedAt;
    }
}