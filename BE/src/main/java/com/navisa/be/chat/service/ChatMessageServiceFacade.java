package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileQueryService;
import com.navisa.be.chat.dto.response.ChatMessageCountResponse;
import com.navisa.be.chat.dto.response.ChatMessageSimpleResponse;
import com.navisa.be.chat.exception.ChatMessageException;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceFacade {

    private final UserQueryService userQueryService;
    private final ForeignerQueryService foreignerQueryService;
    private final AgentProfileQueryService agentProfileQueryService;
    private final ChatMessageQueryService chatMessageQueryService;
    private final ChatRoomQueryService chatRoomQueryService;

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

    @Transactional(readOnly = true)
    public SliceResponse<ChatMessageSimpleResponse, Long> findChatMessagesByChatRoomIdAndNoOffset(
            String email, Long roomId, SliceRequest<Long> slice) {

        User findUser = userQueryService.findByEmail(email);

        UUID profileId = findProfileId(findUser, roomId);

        List<ChatMessage> chatMessageList = chatMessageQueryService.findChatMessagesByChatRoomIdAndNoOffset(roomId, slice);

        boolean existsNext = chatMessageList.size() > slice.size();

        List<ChatMessage> contentChatMessages = existsNext
                ? chatMessageList.subList(0, slice.size())
                : chatMessageList;

        Long lastElementId = contentChatMessages.isEmpty() ? null : contentChatMessages.get(contentChatMessages.size() - 1).getId();

        return new SliceResponse<>(
                contentChatMessages
                        .stream().map(chatMessage -> ChatMessageSimpleResponse.entityToDto(chatMessage, profileId))
                        .toList(),
                existsNext,
                lastElementId);
    }

    private UUID findProfileId(User findUser, Long roomId) {
        if (findUser.getUserType().equals(UserType.FILLED_FOREIGNER)) {
            ForeignerProfile foreignerProfile = foreignerQueryService.findByUserId(findUser.getId());
            if (!chatRoomQueryService.isOwnedByProfileIdAndChatRoomId(roomId, foreignerProfile)) {
                throw new ChatMessageException(ResponseStatus.NOT_ALLOWED_TO_GET_CHAT_MESSAGE);
            }
            return foreignerProfile.getId();
        }

        AgentProfile agentProfile = agentProfileQueryService.findByUserId(findUser.getId());
        if (!chatRoomQueryService.isOwnedByProfileIdAndChatRoomId(roomId, agentProfile)) {
            throw new ChatMessageException(ResponseStatus.NOT_ALLOWED_TO_GET_CHAT_MESSAGE);
        }
        return agentProfile.getId();
    }
}
