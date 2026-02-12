package com.navisa.be.foreigner.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외국인 유저 서비스 진행 상태 응답")
public record ForeignerProgressResponse(
        @Schema(description = "리뷰 작성 여부")
        boolean isReview,

        @Schema(description = "피드백 작성 여부")
        boolean isFeedback,

        @Schema(description = "수임 완료 여부")
        boolean isFinished,

        @Schema(description = "매칭 완료 여부")
        boolean isMatched,

        @Schema(description = "연결된 채팅방 고유 ID (매칭 전일 경우 null)")
        Long chatRoomId
) {}
