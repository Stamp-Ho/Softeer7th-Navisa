package com.navisa.be.chat.service;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomFilterType;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.common.dto.request.SliceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final ChatRoomRepository chatRoomRepository;

    // 외국인이 자신의 채팅방을 조회
    public List<ChatRoom> findChatRoomByProfileId(
            UUID foreignerId, SliceRequest<Long> slice, boolean isForeignerId, ChatRoomFilterType filter) {

        return chatRoomRepository.findByNoOffset(foreignerId, slice, isForeignerId, filter);
    }

    public List<Long> findChatRoomsByForeignerId(UUID foreignerId) {
        return chatRoomRepository.findAllIdsByForeignerProfileId(foreignerId);
    }

    public List<Long> findChatRoomsByAgentId(UUID agentId) {
        return chatRoomRepository.findAllIdsByAgentProfileId(agentId);
    }
}
