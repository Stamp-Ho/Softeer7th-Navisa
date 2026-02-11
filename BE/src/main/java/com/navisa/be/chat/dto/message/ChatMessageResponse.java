package com.navisa.be.chat.dto.message;

import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.enums.MessageType;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ChatMessageResponse(
        Long messageId,         // 고유 번호
        Long roomId,            // 채팅방 ID
        UUID clientMessageId,   // 프론트에서 메시지를 식별하기 위해 사용하는 ID
        UUID receiverId,     // 수신자가 보낸 메시지인지 여부
        String content,         // 메시지 내용 or 비자신청서 ID String
        MessageType type,       // 메시지 타입
        ZonedDateTime sentAt,   // 클라이언트가 보낸 시간 (ISO 8601)
        LocalDateTime createdAt // 서버 DB 저장 시간
) {

    public static ChatMessageResponse entityToDto(ChatMessage chatMessage, ChatMessageRequest request, UUID receiverId) {
        return new ChatMessageResponse(
            chatMessage.getId(),
            request.roomId(),
            request.clientMessageId(),
            receiverId,
            request.content(),
            request.type(),
            request.sentAt(),
            chatMessage.getCreatedAt()
        );
    }
}