package com.navisa.be.chat.service;

import com.navisa.be.chat.dto.projection.ChatMessageNonReadCountProjection;
import com.navisa.be.chat.dto.projection.LastMessageProjection;
import com.navisa.be.chat.model.entity.ChatMessage;
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
public class ChatMessageSearchService {
    private final ChatMessageRepository chatMessageRepository;

    public List<LastMessageProjection> findAllLastChatMessageByChatRoomIn(List<Long> contentChatRoomIds) {
        return chatMessageRepository.findAllLastChatMessageGroupByChatRoom(contentChatRoomIds);
    }

    public List<ChatMessageNonReadCountProjection> findCountByChatRoomIdsIn(List<Long> contentChatRoomIds, UUID profileId) {
        return chatMessageRepository.findCountByChatRoomIdsIn(contentChatRoomIds, profileId);
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
