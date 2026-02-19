package com.navisa.be.chat.dto.projection;

import com.navisa.be.chat.model.enums.ChatRoomStatus;

import java.time.ZonedDateTime;

public record ChatRoomInfoProjection(
        Long chatRoomId,
        ChatRoomStatus status,
        ZonedDateTime lastChattedAt,
        String partnerName,
        String partnerProfileImageKey) {
}
