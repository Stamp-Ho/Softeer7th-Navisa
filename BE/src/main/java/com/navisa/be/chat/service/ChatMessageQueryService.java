package com.navisa.be.chat.service;

import com.navisa.be.chat.dto.projection.ChatMessageNonReadCountProjection;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.repository.ChatMessageRepository;
import com.navisa.be.global.web.request.SliceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageQueryService {
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> findAllLastChatMessageByChatRoomIn(List<Long> contentChatRoomIds) {
        return chatMessageRepository.findAllLastChatMessageGroupByChatRoom(contentChatRoomIds);
    }

    public List<ChatMessageNonReadCountProjection> findCountByChatRoomIn(List<ChatRoom> contentChatRooms, UUID profileId) {
        return chatMessageRepository.findCountByChatRoomIn(contentChatRooms, profileId);
    }

    public Long findNonReadCountByProfileId(UUID profileId, boolean isForeigner) {
        return chatMessageRepository.findNonReadCountByProfileId(profileId, isForeigner);
    }

    public Long findMatchedNonReadCountByAgentId(UUID agentId) {
        return chatMessageRepository.findMatchedNonReadCountByAgentId(agentId);
    }

    public List<ChatMessage> findChatMessagesByChatRoomIdAndNoOffset(Long roomId, SliceRequest<Long> slice) {
        return chatMessageRepository.findChatMessagesByChatRoomIdAndNoOffset(roomId, slice);
    }
}
