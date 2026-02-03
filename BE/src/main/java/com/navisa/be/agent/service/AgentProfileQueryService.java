package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.AgentCardQueryDto;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.exception.AgentProfileDomainException;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.storage.service.AwsCloudfrontService;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AgentProfileQueryService {

    private final AgentProfileRepository agentProfileRepository;
    private final AgentBadgeService agentBadgeService;
    private final AgentSpecializedJobService agentSpecializedJobService;
    private final AwsCloudfrontService awsCloudfrontService;

    public AgentProfileQueryService(AgentProfileRepository agentProfileRepository, AgentBadgeService agentBadgeService,
                                    AgentSpecializedJobService agentSpecializedJobService, AwsCloudfrontService awsCloudfrontService) {
        this.agentProfileRepository = agentProfileRepository;
        this.agentBadgeService = agentBadgeService;
        this.agentSpecializedJobService = agentSpecializedJobService;
        this.awsCloudfrontService = awsCloudfrontService;
    }

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
                                com.navisa.be.storage.model.enums.ImageSize.SMALL,
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
                .orElseThrow(() -> new AgentProfileDomainException(ResponseStatus.INVALID_USER));
    }
}
