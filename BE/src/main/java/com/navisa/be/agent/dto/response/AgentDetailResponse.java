package com.navisa.be.agent.dto.response;

import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Schema(description = "행정사 응답 dto")
public record AgentDetailResponse(
        AgentDetailHeaderDto header,
        AgentDetailExpertiseDto expertise,
        AgentDetailInfoDto agentInfo,
        @Schema(description = "행정사 추가 이력")
        String additionalHistory,
        AgentDetailReviewSummaryDto reviewSummary,
        AgentDetailOfficeInfo officeInfo
) {

    public static AgentDetailResponse entityToDto(AgentProfile agentProfile, List<AgentBadgeSummary> top6BadgeSummary, String agentProfileImageUrl, Optional<ChatRoom> optChatRoom, long reviewCount){
        return new AgentDetailResponse(
                AgentDetailHeaderDto.entityToDto(agentProfile, top6BadgeSummary),
                AgentDetailExpertiseDto.entityToDto(agentProfile),
                AgentDetailInfoDto.entityToDto(agentProfile, agentProfile.getLastLoginAt(), agentProfileImageUrl, optChatRoom),
                agentProfile.getAdditionalHistory(),
                AgentDetailReviewSummaryDto.entityToDto(reviewCount, top6BadgeSummary),
                AgentDetailOfficeInfo.entityToDto(agentProfile)
        );
    }

    @Schema(description = "행정사 상세 조회 헤더 dto")
    public record AgentDetailHeaderDto(
            @Schema(description = "상위 2개 뱃지 id")
            List<Long> top2badgeIds,
            @Schema(description = "행정사 한마디")
            String comment
    ) {
        public static AgentDetailHeaderDto entityToDto(AgentProfile agentProfile, List<AgentBadgeSummary> badgeSummarys) {
            if (badgeSummarys == null) {
                return new AgentDetailHeaderDto(List.of(), agentProfile.getComment());
            }

            List<Long> top2BadgeIds = badgeSummarys.stream()
                    .map(summary -> summary.getBadge().getId())
                    .limit(2)
                    .toList();

            return new AgentDetailHeaderDto(top2BadgeIds, agentProfile.getComment());
        }
    }

    @Schema(description = "행정사 전문분야 dto")
    public record AgentDetailExpertiseDto(
            @Schema(description = "행정사 특화직무 id")
            List<Long> jobCodeIds,
            @Schema(description = "행정사 사용가능 언어 id")
            List<Long> languageIds
    ) {
        public static AgentDetailExpertiseDto entityToDto(AgentProfile agentProfile) {
            List<AgentSpecializedJob> jobCodes = agentProfile.getSpecializedJobs();
            List<AgentLanguage> languages = agentProfile.getLanguages();
            List<Long> jobCodeIds = jobCodes == null ? List.of() : jobCodes.stream().map(jobCode -> jobCode.getJobCode().getId()).toList();
            List<Long> languageIds = languages == null ? List.of() : languages.stream().map(lang -> lang.getLanguage().getId()).toList();
            return new AgentDetailExpertiseDto(jobCodeIds, languageIds);
        }
    }

    @Schema(description = "행정사 상세 정보 DTO")
    public record AgentDetailInfoDto(
            @Schema(description = "행정사 id")
            UUID agentId,
            @Schema(description = "행정사 이름")
            String name,
            @Schema(description = "행정사 프로필 이미지")
            String profileImageUrl,
            @Schema(description = "행정사 최근 로그인 시간")
            ZonedDateTime lastLoginAt,
            @Schema(description = "채팅방 유무")
            Boolean hasChatRoom,
            @Schema(description = "채팅방 차단 여부")
            Boolean hasBlocked,
            @Schema(description = "채팅방 id")
            Long chatRoomId
    ) {
        public static AgentDetailInfoDto entityToDto(AgentProfile agentProfile, ZonedDateTime agentUserLastLoginAt, String agentProfileImageUrl, Optional<ChatRoom> optChatRoom) {
            return optChatRoom.map(room -> new AgentDetailInfoDto(
                            agentProfile.getId(),
                            agentProfile.getName(),
                            agentProfileImageUrl,
                            agentUserLastLoginAt,
                            true,
                            room.getStatus() == ChatRoomStatus.BLOCKED,
                            room.getId())
                    )
                    .orElseGet(() -> new AgentDetailInfoDto(
                            agentProfile.getId(),
                            agentProfile.getName(),
                            agentProfileImageUrl,
                            agentUserLastLoginAt,
                            false,
                            false,
                            null)
                    );
        }
    }

    @Schema(description = "행정사 리뷰 요약 dto")
    public record AgentDetailReviewSummaryDto(
            @Schema(description = "행정사 총 리뷰 수")
            Long totalCount,
            @Schema(description = "행정사 상위 6개 배지")
            List<AgentDetailBadge> strengths
    ) {
        public static AgentDetailReviewSummaryDto entityToDto(long reviewCount, List<AgentBadgeSummary> badgeSummarys) {
            if (badgeSummarys == null) {
                return new AgentDetailReviewSummaryDto(reviewCount, List.of());
            }

            List<AgentDetailReviewSummaryDto.AgentDetailBadge> top6badges = badgeSummarys.stream()
                    .map(summary -> new AgentDetailReviewSummaryDto.AgentDetailBadge(
                            summary.getBadge().getId(), summary.getCount()))
                    .toList();
            return new AgentDetailReviewSummaryDto(reviewCount, top6badges);
        }

        public record AgentDetailBadge(
                @Schema(description = "배지 id")
                Long badgeId,
                @Schema(description = "배지 수")
                Integer badgeCount
        ) {
        }
    }

    @Schema(description = "행정사 사무소 정보 dto")
    public record AgentDetailOfficeInfo(
            @Schema(description = "사무실 이름")
            String officeName,
            @Schema(description = "사무실 주소")
            String address,
            @Schema(description = "상세 주소")
            String officeAddressDetail,
            @Schema(description = "영업시간")
            String businessHours,
            @Schema(description = "전화번호")
            String phoneNumber
    ) {
        public static AgentDetailOfficeInfo entityToDto(AgentProfile agentProfile) {
            return new AgentDetailOfficeInfo(agentProfile.getOfficeName(),
                    agentProfile.getOfficeAddress(),
                    agentProfile.getOfficeAddressDetail(),
                    agentProfile.getBusinessTime(),
                    agentProfile.getPhoneNumber());
        }
    }
}
