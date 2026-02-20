package com.navisa.be.chat.dto.message;

import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.MessageType;

import java.util.UUID;

public record ChatMessageRequest(
        Long roomId,          // 채팅방 id
        UUID clientMessageId, // 프론트에서 메시지를 식별하기 위해 사용하는 ID
        String content,       // 텍스트 메시지 내용or 비자신청서 ID String
        MessageType type     // 메시지 타입
) {

    public ChatMessage dtoToEntity(ChatRoom chatRoom, UUID senderId) {
        return new ChatMessage(chatRoom, type, content, senderId);
    }

    public ChatMessageRequest updateContent(String newContent) {
        return new ChatMessageRequest(this.roomId, this.clientMessageId, newContent, this.type);
    }
}