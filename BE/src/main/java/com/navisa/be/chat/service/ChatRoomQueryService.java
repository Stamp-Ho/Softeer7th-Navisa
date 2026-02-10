package com.navisa.be.chat.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomFilterType;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.common.dto.request.SliceRequest;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
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

    public ChatRoom findById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM));
    }

    public boolean isOwnedByProfileIdAndChatRoomId(Long roomId, ForeignerProfile foreignerProfile) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM))
                .getForeignerProfile().getId().equals(foreignerProfile.getId());
    }

    public boolean isOwnedByProfileIdAndChatRoomId(Long roomId, AgentProfile agentProfile) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.INVALID_CHATROOM))
                .getAgentProfile().getId().equals(agentProfile.getId());
    }
}
