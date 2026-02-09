package com.navisa.be.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Schema(description = "행정사 상세 정보 id")
public record AgentDetailInfoDto(
        @Schema(description = "행정사 id")
        UUID agentId,
        @Schema(description = "행정사 이름")
        String name,
        @Schema(description = "행정사 프로필 이미지")
        String profileImageUrl,
        @Schema(description = "행정사 최근 로그인 시간")
        ZonedDateTime lastLoginAt,
        @Schema(description = "채팅방 유뮤")
        Boolean hasChatRoom,
        @Schema(description = "채팅방 차단 여부")
        Boolean hasBlocked,
        @Schema(description = "채팅방 id")
        Long chatRoomId
) {
}
