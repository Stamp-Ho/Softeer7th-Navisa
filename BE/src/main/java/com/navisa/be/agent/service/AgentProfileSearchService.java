package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentCardQueryDto;
import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.AgentDetailResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.service.ChatRoomQueryService;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.global.common.service.JobGroupService;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AgentProfileSearchService {

    private final JobGroupService jobGroupService;
    private final UserQueryService userQueryService;
    private final AgentProfileRepository agentProfileRepository;
    private final AgentSpecializedJobService agentSpecializedJobService;
    private final AgentBadgeService agentBadgeService;
    private final StorageService storageService;
    private final ChatRoomQueryService chatRoomQueryService;
    private final AgentReviewRepository agentReviewRepository;
    private final ForeignerQueryService foreignerQueryService;

    public SliceResponse<AgentCardResponse, UUID> findAgentProfileCardsBasedOnFilter(
            AgentCardRequest request, SliceRequest<UUID> slice, String email) {

        List<Long> jobCodeIds = jobGroupService.findAllJobCodeIdsByGroupNames(request.jobGroupNameList());

        UserType requestUserType = userQueryService.findByEmail(email).getUserType();

        AgentCardQueryDto dto = new AgentCardQueryDto(jobCodeIds, request.regionList(), request.languageIdList());

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

    /*
     * 외국인 입장 행정사 조회
     * */
    public AgentDetailResponse getAgentDetail(String loginUserEmail, UUID agentId) {
        // 행정사가 없으면 예외 발생
        AgentProfile agentProfile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new AgentException(ResponseStatus.AGENT_NOT_FOUND));
        List<AgentBadgeSummary> top6BadgeSummary = agentBadgeService.getTopKBadgeByAgentId(agentId, 6);

        String agentProfileImageUrl = storageService.getImgUrl(ImageSize.MEDIUM, agentProfile.getProfileObjectKey(), false);

        // 보는 사람이 외국인이면 채팅방 정보 제공
        Optional<ChatRoom> optChatRoom = Optional.empty();
        User loginUser = userQueryService.findByEmail(loginUserEmail);
        if(loginUser.getUserType() == UserType.FILLED_FOREIGNER){
            ForeignerProfile foreignerProfile = foreignerQueryService.findByUserId(loginUser.getId());
            optChatRoom = chatRoomQueryService.findByAgentIdAndForeignerId(agentId, foreignerProfile.getId());
        }

        long reviewCount = agentReviewRepository.countByAgentProfileId(agentId);

        return AgentDetailResponse.entityToDto(agentProfile, top6BadgeSummary, agentProfileImageUrl, optChatRoom, reviewCount);
    }
}
