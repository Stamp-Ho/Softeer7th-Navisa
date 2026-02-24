package com.navisa.be.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "채팅방 요청 DTO")
public record ChatRoomCreateRequest(
        @Schema(description = "상대방 프로필 id")
        @NotNull(message = "상대방 id는 null일 수 없습니다")
        UUID opponentProfileId,

        @Schema(description = "첫 채팅 메시지 내용")
        @NotBlank(message = "채팅 내용은 비어 있을 수 없습니다")
        String content
) {
}
