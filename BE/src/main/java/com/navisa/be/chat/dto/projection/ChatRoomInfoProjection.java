package com.navisa.be.chat.dto.projection;

import com.navisa.be.chat.model.enums.ChatRoomStatus;

import java.time.LocalDateTime;

public record ChatRoomInfoProjection(
        Long chatRoomId,
        ChatRoomStatus status,
        LocalDateTime lastChattedAt,
        String partnerName,
        String partnerProfileImageKey) {
}
