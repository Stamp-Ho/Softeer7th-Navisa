package com.navisa.be.chat.service;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandService {

    public void updateStatus(ChatRoom chatRoom) {
        chatRoom.updateStatus(ChatRoomStatus.BLOCKED);
    }
}
