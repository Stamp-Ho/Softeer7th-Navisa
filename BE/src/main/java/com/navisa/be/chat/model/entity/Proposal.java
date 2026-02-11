package com.navisa.be.chat.model.entity;

import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "proposal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Proposal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProposalStatus status = ProposalStatus.PROPOSED;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    public Proposal(ChatRoom chatRoom, UUID senderId) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
    }

    public void updateStatus(ProposalStatus status) {
        this.status = status;
    }
}
