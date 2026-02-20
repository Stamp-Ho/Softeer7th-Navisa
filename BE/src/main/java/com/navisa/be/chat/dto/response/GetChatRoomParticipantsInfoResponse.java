package com.navisa.be.chat.dto.response;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.foreigner.model.entity.ForeignerExpectedCompany;
import com.navisa.be.foreigner.model.entity.ForeignerNationality;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "특정 채팅방 참여자 정보 조회 응답 DTO")
public record GetChatRoomParticipantsInfoResponse(
        AgentInfo agentInfo,
        ForeignerInfo foreignerInfo
) {

    public static GetChatRoomParticipantsInfoResponse entityToDto(
            AgentProfile agentProfile,
            List<Long> top2BadgeIds,
            ForeignerProfile foreignerProfile,
            ForeignerExpectedCompany expectedCompany,
            List<Long> foreignerNationalityIds,
            Boolean isReviewRequired,
            UUID applicationFormId
    ) {
        return new GetChatRoomParticipantsInfoResponse(
                new AgentInfo(
                        agentProfile.getId(),
                        top2BadgeIds,
                        agentProfile.getName(),
                        applicationFormId
                ),
                new ForeignerInfo(
                        foreignerProfile.getId(),
                        foreignerProfile.getNickname(),
                        expectedCompany.getJobTitle(),
                        expectedCompany.getStartDate(),
                        foreignerNationalityIds,
                        isReviewRequired
                )
        );
    }

    @Schema(description = "행정사 정보")
    public record AgentInfo(
            @Schema(description = "행정사 id")
            UUID agentId,
            @Schema(description = "많이 받은 배지 id 리스트")
            List<Long> top2BadgeIds,
            @Schema(description = "행정사 이름")
            String name,
            @Schema(description = "행정사가 위임받은 비자신청서 id")
            UUID applicationFormId
    ) {

    }

    @Schema(description = "외국인 정보")
    public record ForeignerInfo(
            @Schema(description = "외국인 id")
            UUID foreignerId,
            @Schema(description = "외국인 닉네임")
            String nickname,
            @Schema(description = "외국인 희망 직무명")
            String expectedJob,
            @Schema(description = "입사 예정일")
            LocalDate expectedStartDate,
            @Schema(description = "국적 id 리스트")
            List<Long> nationalityIds,
            @Schema(description = "리뷰 필요 여부")
            Boolean isReviewRequired
    ) {

    }
}
