package com.navisa.be.chat.dto.response;

import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;

import java.time.LocalDateTime;

public record ChatRoomCardResponse(
        Long chatRoomId,
        String profileImgUrl,
        String opponentName,
        ChatRoomStatus roomStatus,
        String lastMessage,
        Long noneReadCount,
        LocalDateTime lastChattedAt,
        Boolean proposed,
        Boolean proposalMatched
) {
    public static ChatRoomCardResponse entityToDto(ChatRoom chatRoom,
                                                   String profileImgUrl,
                                                   String lastMessage,
                                                   Long noneReadCount,
                                                   boolean isForeigner,
                                                   boolean proposed,
                                                   boolean proposalMatched) {

        return new ChatRoomCardResponse(
                chatRoom.getId(),
                profileImgUrl,
                isForeigner ? chatRoom.getAgentProfile().getName() : chatRoom.getForeignerProfile().getNickname(),
                chatRoom.getStatus(),
                lastMessage,
                noneReadCount,
                chatRoom.getLastChattedAt(),
                proposed,
                proposalMatched
        );
    }
}
