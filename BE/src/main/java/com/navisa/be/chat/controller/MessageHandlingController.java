package com.navisa.be.chat.controller;

import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.chat.service.ChatServiceFacade;
import com.navisa.be.common.model.enums.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageHandlingController {

    private final ChatServiceFacade chatServiceFacade;

    @MessageMapping("/chat/message") // 클라이언트가 /pub/chat/message로 보낼 때 매칭
    public void handleChatMessage(@Payload ChatMessageRequest request, StompHeaderAccessor headerAccessor) {
        log.info("SEND 컨트롤러 진입 - roomId: {}", request.roomId());

        // 세션에서 저장해둔 userId 추출
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null || !sessionAttributes.containsKey("userId")) {
            throw new WebSocketConnectionException(ResponseStatus.INVALID_CHATTING_SESSION);
        }
        UUID senderId;
        try {
            senderId = UUID.fromString((String) sessionAttributes.get("userId"));
        } catch (IllegalArgumentException | ClassCastException e) {
            throw new WebSocketConnectionException(ResponseStatus.INVALID_CHATTING_SESSION);
        }

        log.debug("request {}", request);

        chatServiceFacade.saveAndPublishMessage(senderId, request);

        log.info("SEND 처리 완료");
    }
}