package com.navisa.be.chat.dto.message;

import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.enums.MessageType;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ChatMessageResponse(
        Long messageId,         // 고유 번호
        Long roomId,            // 채팅방 ID
        UUID clientMessageId,   // FE에서 메시지를 식별하기 위해 사용하는 ID
        UUID senderId,          // FE에서 송신자를 식별하기 위한 행정사/외국인 프로필 ID
        UUID receiverId,        // 메시지를 수신할 클라이언트의 세션을 식별하기 위한 userId
        String content,         // 메시지 내용 or 비자신청서 ID String
        MessageType type,       // 메시지 타입
        ZonedDateTime sentAt,   // 클라이언트가 보낸 시간 (ISO 8601)
        LocalDateTime createdAt // 서버 DB 저장 시간
) {

    public static ChatMessageResponse entityToDto(ChatMessage chatMessage, ChatMessageRequest request, UUID senderId, UUID receiverId) {
        return new ChatMessageResponse(
            chatMessage.getId(),
            request.roomId(),
            request.clientMessageId(),
            senderId,
            receiverId,
            request.content(),
            request.type(),
            request.sentAt(),
            chatMessage.getCreatedAt()
        );
    }
}