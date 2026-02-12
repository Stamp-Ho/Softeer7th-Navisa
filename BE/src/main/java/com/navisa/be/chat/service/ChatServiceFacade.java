package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.message.ChatMessageResponse;
import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatServiceFacade {

    private final ChatMessageService chatMessageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomQueryService chatRoomQueryService;

    public void saveAndPublishMessage(UUID senderId, ChatMessageRequest request) {
        ChatRoom chatRoom = chatRoomQueryService.findByIdWithProfiles(request.roomId());
        UUID senderProfileId = getSenderProfileId(chatRoom, senderId);
        ChatMessage chatMessage = chatMessageService.create(chatRoom, senderProfileId, request);

        // 송신자의 채널에 에코
        log.debug("senderId {}", senderId);
        ChatMessageResponse echoResponse = ChatMessageResponse.entityToDto(chatMessage, request, senderId);
        redisTemplate.convertAndSend("user:ch:" + senderId, echoResponse);

        // 수신자의 채널에 발행
        UUID receiverId = getReceiverId(senderId, chatRoom);
        log.debug("receiverId {}", receiverId);
        ChatMessageResponse response = ChatMessageResponse.entityToDto(chatMessage, request, receiverId);
        redisTemplate.convertAndSend("user:ch:" + receiverId, response);
    }

    private UUID getSenderProfileId(ChatRoom chatRoom, UUID senderId) {
        if(chatRoom.getAgentProfile().getUserId().equals(senderId)){
            return chatRoom.getAgentProfile().getId();
        }
        if(chatRoom.getForeignerProfile().getUserId().equals(senderId)){
            return chatRoom.getForeignerProfile().getId();
        }
        throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
    }

    private static UUID getReceiverId(UUID senderId, ChatRoom chatRoom) {
        if(chatRoom.getAgentProfile().getUserId().equals(senderId)){
            ForeignerProfile foreignerProfile = chatRoom.getForeignerProfile();
            return foreignerProfile.getUserId();
        }
        AgentProfile agentProfile = chatRoom.getAgentProfile();
        return agentProfile.getUserId();
    }
}
