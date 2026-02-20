package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.projection.AgentSimpleProjection;
import com.navisa.be.agent.dto.AgentCardQueryDto;
import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.dto.response.AgentDetailResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentBadgeSummary;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentReviewRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.service.ChatRoomCrudService;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.common.service.StorageService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.common.service.JobGroupService;
import com.navisa.be.global.common.model.enums.ImageSize;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AgentProfileSearchService {

    private final JobGroupService jobGroupService;
    private final UserCrudService userCrudService;
    private final AgentProfileRepository agentProfileRepository;
    private final AgentSpecializedJobService agentSpecializedJobService;
    private final AgentBadgeService agentBadgeService;
    private final StorageService storageService;
    private final ChatRoomCrudService chatRoomCrudService;
    private final AgentReviewRepository agentReviewRepository;
    private final ForeignerProfileCrudService foreignerProfileCrudService;

    public SliceResponse<AgentCardResponse, UUID> findAgentProfileCardsBasedOnFilter(
            AgentCardRequest request, SliceRequest<UUID> slice, String email) {

        List<Long> jobCodeIds = jobGroupService.findAllJobCodeIdsByGroupNames(request.jobGroupNameList());

        UserType requestUserType = userCrudService.findByEmail(email).getUserType();

        AgentCardQueryDto dto = new AgentCardQueryDto(jobCodeIds, request.regionList(), request.languageIdList());

        List<AgentSimpleProjection> agentProjections = agentProfileRepository.findByFilters(dto, slice);

        boolean existsNext = agentProjections.size() > slice.size();

        List<AgentSimpleProjection> contentProjections = existsNext
                ? agentProjections.subList(0, slice.size())
                : agentProjections;

        List<UUID> contentProfileIds = contentProjections.stream().map(AgentSimpleProjection::agentId).toList();

        Map<UUID, List<Long>> agentSpecialityTop2 = agentSpecializedJobService.getTop2SpecializedJobIdsBatch(contentProfileIds);

        Map<UUID, List<Long>> badgeTop2Map = agentBadgeService.getTop2BadgeIdsBatch(contentProfileIds);

        List<AgentCardResponse> content = contentProjections.stream()
                .map(agent -> {
                    String profileUrl = storageService.getImgUrl(
                            ImageSize.SMALL,
                            agent.profileObjectKey(),
                            false);

                    return AgentCardResponse.projectionToDto(
                            agent,
                            profileUrl,
                            agentSpecialityTop2.getOrDefault(agent.agentId(), List.of()),
                            badgeTop2Map.getOrDefault(agent.agentId(), List.of()),
                            requestUserType);
                })
                .toList();

        UUID lastElementId = content.isEmpty() ? null : content.get(content.size() - 1).agentId();

        return new SliceResponse<>(content, existsNext, lastElementId);
    }

    /*
     * 외국인 입장 행정사 조회
     */
    public AgentDetailResponse getAgentDetail(String loginUserEmail, UUID agentId) {
        // 행정사가 없으면 예외 발생
        AgentProfile agentProfile = agentProfileRepository.findById(agentId)
                .orElseThrow(() -> new AgentException(ResponseStatus.AGENT_NOT_FOUND));
        List<AgentBadgeSummary> top6BadgeSummary = agentBadgeService.getTopKBadgeByAgentId(agentId, 6);

        String agentProfileImageUrl = storageService.getImgUrl(ImageSize.MEDIUM,
                agentProfile.getProfileObjectKey(), false);

        // 보는 사람이 외국인이면 채팅방 정보 제공
        Optional<ChatRoom> optChatRoom = Optional.empty();
        User loginUser = userCrudService.findByEmail(loginUserEmail);
        if (loginUser.getUserType() == UserType.FILLED_FOREIGNER) {
            ForeignerProfile foreignerProfile = foreignerProfileCrudService.findByUserId(loginUser.getId());
            optChatRoom = chatRoomCrudService.findOptionalByAgentIdAndForeignerId(agentId, foreignerProfile.getId());
        }

        long reviewCount = agentReviewRepository.countByAgentProfileId(agentId);

        return AgentDetailResponse.entityToDto(agentProfile, top6BadgeSummary, agentProfileImageUrl,
                optChatRoom, reviewCount);
    }
}
