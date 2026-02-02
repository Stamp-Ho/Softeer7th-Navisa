package com.navisa.be.foreigner.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.UUID;

@Schema(description = "외국인 상세 요건 입력 상태 응답")
public record ForeignerStatusResponse(
        @Schema(description = "외국인 프로필 고유 ID")
        UUID foreignerProfileId,

        @Schema(description = "추천 요건 작성 완료 여부")
        boolean isCompletedRecommendation
) {}
