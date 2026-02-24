package com.navisa.be.chat.service;

import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageRegistrationService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessage createFirstTextMessage(ChatRoom chatRoom, UUID senderId, ChatMessageRequest request) {
        ChatMessage message = chatMessageRepository.save(request.dtoToEntity(chatRoom, senderId));
        chatMessageRepository.flush();
        return message;
    }

    @Transactional
    public ChatMessage createFirstTextMessage(ChatRoom chatRoom, UUID senderId, String content){
        ChatMessage message = chatMessageRepository.save(new ChatMessage(chatRoom, MessageType.TEXT, content, senderId));
        chatMessageRepository.flush();
        return message;
    }

    @Transactional
    public void updateReadStatusBeforeChatMessageSentAt(Long chatMessageId, UUID profileId, Long chatRoomId) {
        chatMessageRepository.updateReadStatusBeforeChatMessageSentAt(chatMessageId, profileId, chatRoomId);
    }
}
