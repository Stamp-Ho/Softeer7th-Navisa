package com.navisa.be.support;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.repository.ChatMessageRepository;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Transactional
public class ChatRoomTestFixture {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @PersistenceContext
    private EntityManager em;

    public ChatRoom createChatRoom(ForeignerProfile foreignerProfile, AgentProfile agentProfile,
            ChatRoomStatus status) {
        ChatRoom chatRoom = new ChatRoom(foreignerProfile, agentProfile, status);
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom createChatRoom(ForeignerProfile foreignerProfile, AgentProfile agentProfile, ChatRoomStatus status,
            LocalDateTime createdAt) {
        ChatRoom chatRoom = new ChatRoom(foreignerProfile, agentProfile, status);
        chatRoom.updateLastChattedAt(createdAt);
        chatRoom = chatRoomRepository.save(chatRoom); // get ID

        em.flush();
        em.clear();

        em.createNativeQuery(
                "UPDATE chat_room SET created_at = :createdAt, last_chatted_at = :createdAt WHERE chat_room_id = :id")
                .setParameter("createdAt", createdAt)
                .setParameter("id", chatRoom.getId())
                .executeUpdate();

        ReflectionTestUtils.setField(chatRoom, "createdAt", createdAt);
        ReflectionTestUtils.setField(chatRoom, "lastChattedAt", createdAt);
        return chatRoom;
    }

    public ChatMessage createChatMessage(ChatRoom chatRoom, UUID senderId, String content, boolean isReadByOther) {
        ChatMessage message = new ChatMessage(chatRoom, MessageType.TEXT, content, senderId);
        ReflectionTestUtils.setField(message, "isReadByOther", isReadByOther);
        return chatMessageRepository.save(message);
    }
}
