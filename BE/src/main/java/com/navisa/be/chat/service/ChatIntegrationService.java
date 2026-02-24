package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.message.ChatMessageResponse;
import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatIntegrationService {

    private final ChatMessageRegistrationService chatMessageRegistrationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomSearchService chatRoomSearchService;

    @Transactional
    public void saveAndPublishChatMessage(UUID senderId, ChatMessageRequest request, ChatRoom chatRoom) {
        final ChatRoom finalChatRoom = (chatRoom != null)
                ? chatRoom
                : chatRoomSearchService.findByIdWithProfiles(request.roomId());
        ChatMessage chatMessage = chatMessageRegistrationService.createFirstTextMessage(
                finalChatRoom, getSenderProfileId(finalChatRoom, senderId), request);
        finalChatRoom.updateLastChattedAt(chatMessage.getCreatedAt());

        // Redis 발행은 트랜잭션 커밋 후 실행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 송신자의 채널에 에코
                log.debug("{} senderId {}", request.type(), senderId);
                ChatMessageResponse echoResponse = ChatMessageResponse.entityToDto(chatMessage, request, senderId, senderId);
                redisTemplate.convertAndSend("user:ch:" + senderId, echoResponse);

                // 수신자의 채널에 발행
                UUID receiverId = getReceiverId(senderId, finalChatRoom);
                log.debug("{} receiverId {}", request.type(), receiverId);
                ChatMessageResponse response = ChatMessageResponse.entityToDto(chatMessage, request, senderId, receiverId);
                redisTemplate.convertAndSend("user:ch:" + receiverId, response);
            }
        });
    }

    @Transactional
    public void saveAndPublishReadEventMessage(UUID senderId, ChatMessageRequest request) {
        ChatRoom findChatRoom = chatRoomSearchService.findByIdWithProfiles(request.roomId());

        if (request.content() == null || request.content().isBlank()) {
            throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
        }

        long lastReadMessageId;
        try {
            lastReadMessageId = Long.parseLong(request.content());
        } catch (NumberFormatException e) {
            throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
        }

        chatMessageRegistrationService.updateReadStatusBeforeChatMessageSentAt
                (lastReadMessageId, getSenderProfileId(findChatRoom, senderId), findChatRoom.getId());

        // 송신자의 채널에 에코
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.debug("READ senderId : {}", senderId);
                ChatMessageResponse echoResponse = ChatMessageResponse.createReadEventMessage(request, senderId, senderId, findChatRoom.getId());
                redisTemplate.convertAndSend("user:ch:" + senderId, echoResponse);

                // 수신자의 채널에 발행
                UUID receiverId = getReceiverId(senderId, findChatRoom);
                log.debug("READ receiverId : {}", receiverId);
                ChatMessageResponse response = ChatMessageResponse.createReadEventMessage(request, senderId, receiverId, findChatRoom.getId());
                redisTemplate.convertAndSend("user:ch:" + receiverId, response);
            }
        });
    }

    private UUID getSenderProfileId(ChatRoom chatRoom, UUID senderId) {
        if (chatRoom.getAgentProfile().getUserId().equals(senderId)) {
            return chatRoom.getAgentProfile().getId();
        }
        if (chatRoom.getForeignerProfile().getUserId().equals(senderId)) {
            return chatRoom.getForeignerProfile().getId();
        }
        throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
    }

    private UUID getReceiverId(UUID senderId, ChatRoom chatRoom) {
        if (chatRoom.getAgentProfile().getUserId().equals(senderId)) {
            ForeignerProfile foreignerProfile = chatRoom.getForeignerProfile();
            return foreignerProfile.getUserId();
        }
        AgentProfile agentProfile = chatRoom.getAgentProfile();
        return agentProfile.getUserId();
    }

    public void publishEventMessage(UUID receiverId, ChatMessageResponse message) {
        // Redis 발행은 트랜잭션 커밋 후 실행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.debug("{} receiverId : {}", message.type(), receiverId);
                redisTemplate.convertAndSend("user:ch:" + receiverId, message);
            }
        });
    }
}
