package com.navisa.be.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채팅방 생성 응답 DTO")
public record ChatRoomCreateResponse(
        @Schema(description = "채팅방 id")
        Long chatRoomId
) {
}
