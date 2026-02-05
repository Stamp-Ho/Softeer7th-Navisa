package com.navisa.be.chat.dto.response;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;

import java.time.ZonedDateTime;

public record ChatRoomCardResponse(
        Long chatRoomId,
        String profileImgUrl,
        String opponentName,
        ChatRoomStatus roomStatus,
        String lastMessage,
        Long noneReadCount,
        ZonedDateTime lastChattedAt
) {
    public static ChatRoomCardResponse toDto(ChatRoom chatRoom, String profileImgUrl,
                                             String lastMessage, Long noneReadCount, boolean isForeigner) {

        return new ChatRoomCardResponse(
                chatRoom.getId(),
                profileImgUrl,
                isForeigner ? chatRoom.getAgentProfile().getName() : chatRoom.getForeignerProfile().getNickname(),
                chatRoom.getStatus(),
                lastMessage,
                noneReadCount,
                chatRoom.getLastChattedAt()
        );
    }
}
