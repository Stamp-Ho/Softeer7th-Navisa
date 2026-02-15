package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.*;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.GetAgentDetailResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentBadgeSummaryRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.global.infra.aws.AwsCloudfrontClient;
import com.navisa.be.global.web.response.ResponseStatus;
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
    private final AwsCloudfrontClient awsCloudfrontService;
    private final AgentBadgeSummaryRepository agentBadgeSummaryRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final AgentReviewRepository agentReviewRepository;
    private final ForeignerProfileRepository foreignerProfileRepository;
    private final StorageService storageService;

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
                        String profileUrl = storageService.getImgUrl(
                                ImageSize.SMALL,
                                agent.getProfileObjectKey(),
                                false
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
        // 행정사가 없으면 예외 발생
        AgentProfile agentProfile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new AgentException(ResponseStatus.AGENT_NOT_FOUND));
        List<AgentBadgeSummary> top6BadgeSummary = agentBadgeSummaryRepository.findTopKBadgeSummarysByAgentId(agentId, PageRequest.of(0, 6));

        User agentUser = userRepository.findById(agentProfile.getUserId())
                .orElseThrow(() -> new AgentException(ResponseStatus.USER_INVALID));
        String agentProfileImageUrl = storageService.getImgUrl(
                ImageSize.MEDIUM, agentProfile.getProfileObjectKey(), false);

        // 보는 사람이 외국인이면 채팅방 정보 제공
        User loginUser = userRepository.findByEmail(loginUserEmail)
                .orElseThrow(() -> new AgentException(ResponseStatus.USER_INVALID));
        Optional<ChatRoom> optChatRoom = Optional.empty();
        if(loginUser.getUserType() == UserType.FILLED_FOREIGNER){
            ForeignerProfile foreignerProfile = foreignerProfileRepository.findByUserId(loginUser.getId())
                    .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_FOREIGNER));
            optChatRoom = chatRoomRepository.findByAgentIdAndForeignerId(agentId, foreignerProfile.getId());
        }

        long reviewCount = agentReviewRepository.countByAgentProfileId(agentId);

        return new GetAgentDetailResponse(
                AgentDetailHeaderDto.entityToDto(agentProfile, top6BadgeSummary),
                AgentDetailExpertiseDto.entityToDto(agentProfile),
                AgentDetailInfoDto.entityToDto(agentProfile, agentProfile.getLastLoginAt(), agentProfileImageUrl, optChatRoom),
                agentProfile.getAdditionalHistory(),
                AgentDetailReviewSummaryDto.entityToDto(reviewCount, top6BadgeSummary),
                AgentDetailOfficeInfo.entityToDto(agentProfile)
        );
    }

    public AgentProfile findByUserId(UUID userId) {
        return agentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_AGENT));
    }

    public boolean existsByUserId(UUID userId) {
        return agentProfileRepository.existsByUserId(userId);
    }

    public AgentProfile findById(UUID id) {
        return agentProfileRepository.findById(id)
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_AGENT));
    }
}
