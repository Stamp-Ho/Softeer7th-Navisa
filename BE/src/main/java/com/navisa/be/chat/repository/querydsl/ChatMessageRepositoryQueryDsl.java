package com.navisa.be.chat.repository.querydsl;

import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.common.dto.request.SliceRequest;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepositoryQueryDsl {
    Long findNonReadCountByProfileId(UUID profileId, boolean isForeigner);
    Long findMatchedNonReadCountByAgentId(UUID profileId);
    List<ChatMessage> findChatMessagesByChatRoomIdAndNoOffset(Long chatRoomId, SliceRequest<Long> slice);
}
