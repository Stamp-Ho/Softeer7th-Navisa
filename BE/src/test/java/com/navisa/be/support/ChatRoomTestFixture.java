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
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ChatRoomTestFixture {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoom createChatRoom(ForeignerProfile foreignerProfile, AgentProfile agentProfile, ChatRoomStatus status,
            ZonedDateTime time) {
        ChatRoom chatRoom = new ChatRoom(foreignerProfile, agentProfile, status, time);
        return chatRoomRepository.save(chatRoom);
    }

    public ChatMessage createChatMessage(ChatRoom chatRoom, UUID senderId, String content, boolean isReadByOther) {
        ChatMessage message = new ChatMessage(chatRoom, MessageType.TEXT, content, senderId);
        ReflectionTestUtils.setField(message, "isReadByOther", isReadByOther);
        return chatMessageRepository.save(message);
    }
}
