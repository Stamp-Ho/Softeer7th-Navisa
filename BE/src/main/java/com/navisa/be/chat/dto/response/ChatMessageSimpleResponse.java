package com.navisa.be.chat.dto.response;

import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.enums.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageSimpleResponse(
        Long chatMessageId,
        boolean isSentByMe,
        MessageType type,
        String content,
        LocalDateTime createdAt,
        boolean isRead
) {
    public static ChatMessageSimpleResponse entityToDto(ChatMessage message, UUID profileId) {
        return new ChatMessageSimpleResponse(
                message.getId(),
                message.getSenderId().equals(profileId),
                message.getMessageType(),
                message.getContent(),
                message.getCreatedAt(),
                message.getIsReadByOther()
        );
    }
}
