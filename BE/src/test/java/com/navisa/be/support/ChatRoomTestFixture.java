package com.navisa.be.support;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Component
public class ChatRoomTestFixture {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom createChatRoom(ForeignerProfile foreignerProfile, AgentProfile agentProfile, ChatRoomStatus status, ZonedDateTime time) {
        ChatRoom chatRoom = new ChatRoom(foreignerProfile, agentProfile, status, time);
        return chatRoomRepository.save(chatRoom);
    }
}
