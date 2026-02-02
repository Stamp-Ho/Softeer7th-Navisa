package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
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
    private final UserQueryService userQueryService;

    public AgentProfileQueryService(AgentProfileRepository agentProfileRepository, AgentBadgeService agentBadgeService, AgentSpecializedJobService agentSpecializedJobService, UserQueryService userQueryService) {
        this.agentProfileRepository = agentProfileRepository;
        this.agentBadgeService = agentBadgeService;
        this.agentSpecializedJobService = agentSpecializedJobService;
        this.userQueryService = userQueryService;
    }

    public SliceResponse<AgentCardResponse, UUID> findAgentProfileCardsBasedOnFilter(
            AgentCardRequest request, SliceRequest<UUID> slice, String email) {

        List<AgentProfile> agentProfiles = agentProfileRepository.findByFilters(request, slice);

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

        UserType requestUserType = userQueryService.findByEmail(email).getUserType();

        List<AgentCardResponse> content = contentProfiles.stream()
                .map(agent -> AgentCardResponse.of(
                        agent,
                        agentSpecialityTop2.get(agent.getId()),
                        badgeTop2Map.get(agent.getId()),
                        requestUserType
                ))
                .toList();

        UUID lastElementId = content.isEmpty() ? null : content.get(content.size() - 1).agentId();

        return new SliceResponse<>(content, existsNext, lastElementId);
    }
}
