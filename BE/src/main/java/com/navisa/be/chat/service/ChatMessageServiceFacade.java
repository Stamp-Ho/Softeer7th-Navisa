package com.navisa.be.chat.service;

import com.navisa.be.agent.service.AgentProfileQueryService;
import com.navisa.be.chat.dto.response.ChatMessageCountResponse;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceFacade {

    private final UserQueryService userQueryService;
    private final ForeignerQueryService foreignerQueryService;
    private final AgentProfileQueryService agentProfileQueryService;
    private final ChatMessageQueryService chatMessageQueryService;

    @Transactional(readOnly = true)
    public ChatMessageCountResponse findNonReadCountByUserEmail(String email) {
        User findUser = userQueryService.findByEmail(email);

        boolean isForeigner = findUser.getUserType().equals(UserType.FILLED_FOREIGNER);

        UUID profileId = isForeigner ?
                foreignerQueryService.findByUserId(findUser.getId()).getId() :
                agentProfileQueryService.findByUserId(findUser.getId()).getId();

        Long count = chatMessageQueryService.findNonReadCountByProfileId(profileId, isForeigner);

        return new ChatMessageCountResponse(count);
    }

    @Transactional(readOnly = true)
    public ChatMessageCountResponse findMatchedNonReadCountByUserEmail(String email) {
        User findUser = userQueryService.findByEmail(email);

        UUID agentId = agentProfileQueryService.findByUserId(findUser.getId()).getId();

        Long count = chatMessageQueryService.findMatchedNonReadCountByAgentId(agentId);

        return new ChatMessageCountResponse(count);
    }
}
