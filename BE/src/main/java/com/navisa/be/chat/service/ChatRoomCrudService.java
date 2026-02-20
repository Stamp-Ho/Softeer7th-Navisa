package com.navisa.be.chat.service;

import com.navisa.be.chat.exception.ChatRoomException;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChatRoomCrudService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom findByAgentIdAndForeignerId(UUID agentId, UUID foreignerId) {
        return chatRoomRepository.findByAgentIdAndForeignerId(agentId, foreignerId)
                .orElseThrow(() -> new ChatRoomException(ResponseStatus.NOT_FOUND_CHATROOM));
    }

    public Optional<ChatRoom> findOptionalByAgentIdAndForeignerId(UUID agentId, UUID foreignerId) {
        return chatRoomRepository.findByAgentIdAndForeignerId(agentId, foreignerId);
    }
}
