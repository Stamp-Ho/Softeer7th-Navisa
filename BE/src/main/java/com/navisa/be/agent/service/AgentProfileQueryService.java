package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.*;
import com.navisa.be.agent.dto.*;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.GetAgentDetailResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.agent.repository.AgentBadgeSummaryRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.storage.model.enums.ImageSize;
import com.navisa.be.storage.service.AwsCloudfrontService;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AgentProfileQueryService {

    private final AgentProfileRepository agentProfileRepository;
    private final AgentBadgeService agentBadgeService;
    private final AgentSpecializedJobService agentSpecializedJobService;
    private final AwsCloudfrontService awsCloudfrontService;
    private final AgentBadgeSummaryRepository agentBadgeSummaryRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AgentReviewRepository agentReviewRepository;
    private final ForeignerProfileRepository foreignerProfileRepository;

    public SliceResponse<AgentCardResponse, UUID> findAgentProfileCardsBasedOnFilter(
            AgentCardQueryDto dto, SliceRequest<UUID> slice, UserType requestUserType) {

        List<AgentProfile> agentProfiles = agentProfileRepository.findByFilters(dto, slice);

        boolean existsNext = agentProfiles.size() > slice.size();

        List<AgentProfile> contentProfiles = existsNext
                ? agentProfiles.subList(0, slice.size())
                : agentProfiles;

        List<UUID> contentProfileIds = contentProfiles.stream().map(AgentProfile::getId).toList();

        Map<UUID, List<Long>> agentSpecialityTop2 = contentProfileIds.stream()
                .collect(Collectors.toMap(
                        agentId -> agentId,
                        agentSpecializedJobService::getTop2SpecializedJobIds
                ));

        Map<UUID, List<Long>> badgeTop2Map = contentProfileIds.stream()
                .collect(Collectors.toMap(
                        agentId -> agentId,
                        agentBadgeService::getTop2BadgeIds
                ));

        List<AgentCardResponse> content = contentProfiles.stream()
                .map(agent -> {
                        String profileUrl = awsCloudfrontService.getImageUrl(
                                ImageSize.SMALL,
                                agent.getProfileObjectKey()
                        );

                        return AgentCardResponse.of(
                            agent,
                            profileUrl,
                            agentSpecialityTop2.get(agent.getId()),
                            badgeTop2Map.get(agent.getId()),
                            requestUserType
                        );
                })
                .toList();

        UUID lastElementId = content.isEmpty() ? null : content.get(content.size() - 1).agentId();

        return new SliceResponse<>(content, existsNext, lastElementId);
    }

    public AgentProfile findWithSpecializedJobByUserId(UUID userId) {
        return agentProfileRepository.findWithSpecializedJobByUserId(userId)
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_USER));
    }

    /*
    * 외국인 입장 행정사 조회
    * */
    public GetAgentDetailResponse getAgentDetail(String loginUserEmail, UUID agentId) {
        User loginUser = userRepository.findByEmail(loginUserEmail)
                .orElseThrow(() -> new AgentException(ResponseStatus.USER_INVALID));

        // 행정사가 없으면 예외 발생
        AgentProfile agentProfile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new AgentException(ResponseStatus.AGENT_NOT_FOUND));

        List<AgentBadgeSummary> top6BadgeSummary = agentBadgeSummaryRepository.findTopKBadgeSummarysByAgentId(agentId,
                PageRequest.of(0, 6));

        AgentDetailHeaderDto header = getAgentDetailHeader(agentProfile, top6BadgeSummary);

        AgentDetailExpertiseDto expertise = getAgentDetailExpertise(agentProfile);

        AgentDetailInfoDto agentInfo = getAgentDetailInfo(agentId, agentProfile, loginUser);

        AgentDetailReviewSummaryDto reviewSummary = getAgentDetailReviewSummary(agentId, top6BadgeSummary);

        AgentDetailOfficeInfo officeInfo = new AgentDetailOfficeInfo(agentProfile.getOfficeName(),
                agentProfile.getOfficeAddress(),
                agentProfile.getBusinessTime(),
                "010-1234-1234"); // todo 추후에 행정사 프로필에서 전화번호 조회

        return new GetAgentDetailResponse(header, expertise, agentInfo, agentProfile.getAdditionalHistory(),
                reviewSummary, officeInfo);
    }

    private AgentDetailHeaderDto getAgentDetailHeader(AgentProfile agentProfile, List<AgentBadgeSummary> top6BadgeSummary) {
        List<Long> top2BadgeIds = top6BadgeSummary.stream()
                .map(summary -> summary.getBadge().getId())
                .limit(2)
                .toList();

        return new AgentDetailHeaderDto(top2BadgeIds, agentProfile.getComment());
    }

    private AgentDetailExpertiseDto getAgentDetailExpertise(AgentProfile agentProfile) {
        List<AgentSpecializedJob> jobCodes = agentProfile.getSpecializedJobs();
        List<AgentLanguage> languages = agentProfile.getLanguages();
        List<Long> jobCodeIds = jobCodes.stream().map(jobCode -> jobCode.getJobCode().getId()).toList();
        List<Long> languageIds = languages.stream().map(lang -> lang.getLanguage().getId()).toList();
        AgentDetailExpertiseDto expertise = new AgentDetailExpertiseDto(jobCodeIds, languageIds);
        return expertise;
    }

    private AgentDetailReviewSummaryDto getAgentDetailReviewSummary(UUID agentId, List<AgentBadgeSummary> top6BadgeSummary) {
        long reviewCount = agentReviewRepository.countByAgentProfileId(agentId);
        List<AgentDetailReviewSummaryDto.AgentDetailBadge> top6badges = top6BadgeSummary.stream()
                .map(summary -> new AgentDetailReviewSummaryDto.AgentDetailBadge(
                        summary.getBadge().getId(), summary.getCount()))
                .toList();
        AgentDetailReviewSummaryDto reviewSummary = new AgentDetailReviewSummaryDto(reviewCount, top6badges);
        return reviewSummary;
    }

    private AgentDetailInfoDto getAgentDetailInfo(UUID agentId, AgentProfile agentProfile, User loginUser) {
        User user = userRepository.findById(agentProfile.getUserId())
                .orElseThrow(() -> new AgentException(ResponseStatus.USER_INVALID));

        String agentProfileImageUrl = awsCloudfrontService.getImageUrl(ImageSize.MEDIUM, agentProfile.getProfileObjectKey());

        if(loginUser.getUserType() == UserType.FILLED_FOREIGNER){
            // 보는 사람이 외국인이면 채팅방 정보 제공
            ForeignerProfile foreignerProfile = foreignerProfileRepository.findByUserId(loginUser.getId())
                    .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_FOREIGNER));

            Optional<ChatRoom> optChatRoom = chatRoomRepository.findByAgentIdAndForeignerId(agentId, foreignerProfile.getId());
            return optChatRoom.map(room -> new AgentDetailInfoDto(
                            agentProfile.getId(),
                            agentProfile.getName(),
                            agentProfileImageUrl,
                            user.getLastLoginAt(),
                            true,
                            room.getStatus() == ChatRoomStatus.BLOCKED,
                            room.getId())
                    )
                    .orElseGet(() -> new AgentDetailInfoDto(
                            agentProfile.getId(),
                            agentProfile.getName(),
                            agentProfileImageUrl,
                            user.getLastLoginAt(),
                            false,
                            false,
                            null)
                    );
        }

        return getAgentDetailForAgent(agentProfile, user, agentProfileImageUrl);
    }

    private AgentDetailInfoDto getAgentDetailForAgent(AgentProfile agentProfile, User user, String agentProfileImageUrl) {
        return new AgentDetailInfoDto(
                agentProfile.getId(),
                agentProfile.getName(),
                agentProfileImageUrl,
                user.getLastLoginAt(),
                false,
                false,
                null);
    }

    public AgentProfile findByUserId(UUID userId) {
        return agentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_AGENT));
    }
}
