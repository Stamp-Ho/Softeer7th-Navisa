package com.navisa.be.chat.dto.projection;

public record LastMessageProjection(
    Long id,
    Long chatRoomId,
    String content
) {
}
