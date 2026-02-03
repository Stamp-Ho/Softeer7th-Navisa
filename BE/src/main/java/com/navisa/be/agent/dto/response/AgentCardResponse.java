package com.navisa.be.agent.dto.response;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "행정사 카드 정보 응답")
public record AgentCardResponse(
        @Schema(description = "행정사 프로필 UUID", example = "550e8400-e29b-41d4-a716-446655440000") UUID agentId,
        @Schema(description = "행정사 이름", example = "김나비") String agentName,
        @Schema(description = "프로필 이미지 URL") String profileImgUrl,
        @Schema(description = "사무소 주소", example = "서울특별시 강남구 테헤란로...") String officeAddress,
        @Schema(description = "전문 분야 상위 2개 (리뷰가 많은 순서의 job_code_id)", example = "[1, 5]") List<Long> agentSpecialityTop2,
        @Schema(description = "보유 뱃지 상위 2개 (리뷰가 많은 순서의 badge_id)", example = "[3, 2]") List<Long> badgeTop2,
        @Schema(description = "전문 분야 개수") Integer specialityJobCount
) {
    public static AgentCardResponse of(AgentProfile agent, String profileImageUrl, List<Long> specialities, List<Long> badges) {
        return new AgentCardResponse(
                agent.getId(),
                agent.getName(),
                profileImageUrl,
                agent.getOfficeAddress(),
                specialities,
                badges,
                agent.getSpecializedJobCodes().size()
        );
    }

    public static AgentCardResponse of(AgentProfile agent, String profileImageUrl, List<Long> specialities, List<Long> badges, UserType requestUserType) {
        return new AgentCardResponse(
                agent.getId(),
                agent.getName(),
                profileImageUrl,
                agent.getOfficeAddress(),
                requestUserType.equals(UserType.FILLED_FOREIGNER) ? specialities : null,
                badges,
                requestUserType.equals(UserType.FILLED_FOREIGNER) ? agent.getSpecializedJobCodes().size() : null
        );
    }
}