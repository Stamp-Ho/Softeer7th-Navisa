package com.navisa.be.chat.model.entity;

import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.common.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "is_read_by_other", nullable = false)
    private Boolean isReadByOther = false;

    @Column(name = "sent_at", nullable = false)
    private ZonedDateTime sentAt;

    public ChatMessage(ChatRoom chatRoom, MessageType messageType, String content, UUID senderId) {
        this.chatRoom = chatRoom;
        this.messageType = messageType;
        this.content = content;
        this.senderId = senderId;
    }

    public ChatMessage(ChatRoom chatRoom, MessageType messageType, String content, UUID senderId, ZonedDateTime sentAt) {
        this.chatRoom = chatRoom;
        this.messageType = messageType;
        this.content = content;
        this.senderId = senderId;
        this.sentAt = sentAt;
    }

    @PrePersist
    public void prePersist() { // 저장 직전, 현재 시각을 항상 UTC 타임존으로 설정
        this.sentAt = ZonedDateTime.now(ZoneOffset.UTC);
    }
}