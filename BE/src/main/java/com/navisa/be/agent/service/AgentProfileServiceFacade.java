package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.AgentCardQueryDto;
import com.navisa.be.agent.dto.request.AgentCardRequest;
import com.navisa.be.agent.dto.response.AgentCardResponse;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.dto.response.SliceResponse;
import com.navisa.be.info.service.JobGroupService;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AgentProfileServiceFacade {

    private final UserQueryService userQueryService;
    private final AgentProfileQueryService agentProfileQueryService;
    private final JobGroupService jobGroupService;

    public AgentProfileServiceFacade(UserQueryService userQueryService, AgentProfileQueryService agentProfileQueryService, JobGroupService jobGroupService) {
        this.userQueryService = userQueryService;
        this.agentProfileQueryService = agentProfileQueryService;
        this.jobGroupService = jobGroupService;
    }

    @Transactional(readOnly = true)
    public SliceResponse<AgentCardResponse, UUID> findAgentProfileCardsBasedOnFilter(
            AgentCardRequest request, SliceRequest<UUID> slice, String email) {

        List<Long> jobCodeIds = jobGroupService.findAllJobCodeIdsByGroupNames(request.jobGroupNameList());

        UserType requestUserType = userQueryService.findByEmail(email).getUserType();

        AgentCardQueryDto dto = new AgentCardQueryDto(jobCodeIds, request.regionList(), request.languageIdList());

        return agentProfileQueryService.findAgentProfileCardsBasedOnFilter(dto, slice, requestUserType);
    }
}
