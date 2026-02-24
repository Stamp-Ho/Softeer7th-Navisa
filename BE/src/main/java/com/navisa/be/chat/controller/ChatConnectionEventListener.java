package com.navisa.be.chat.controller;

import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.chat.service.ChatSubscribeService;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatConnectionEventListener {

    private final ChatSubscribeService chatSubscribeService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        log.info("[WS Connect] New connection attempt");

        validateSessionAttributesAndUserId(sessionAttributes);

        String userIdStr = (String) sessionAttributes.get("userId");
        UUID userId = UUID.fromString(userIdStr);
        String sessionId = headerAccessor.getSessionId();
        log.info("[WS Connect] User: {}, Session Id: {}, Session Count Incremented", userId, sessionId);
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        log.info("subscribe 진입");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        validateSessionAttributesAndUserId(sessionAttributes);

        if (destination == null || !destination.equals("/user/chat/subscribe")) {
            throw new WebSocketConnectionException(ResponseStatus.ILLEGAL_SUBSCRIBE_DESTINATION);
        }

        String userIdStr = (String) sessionAttributes.get("userId");
        UUID userId = UUID.fromString(userIdStr);
        String sessionId = headerAccessor.getSessionId();

        chatSubscribeService.addChatSubscription(userId, sessionId);

        log.info("subscribe 성공");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("disconnect 진입");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        // 1. 검증 대신 '확인' 후 부족하면 조용히 종료
        if (sessionAttributes == null || !sessionAttributes.containsKey("userId")) {
            log.info("[WS Disconnect] 인증되지 않았거나 세션 정보가 없는 연결 종료 (Session ID: {})", event.getSessionId());
            return;
        }

        // 2. 정보가 확실히 있을 때만 로직 수행
        try {
            String userIdStr = (String) sessionAttributes.get("userId");
            UUID userId = UUID.fromString(userIdStr);
            String sessionId = headerAccessor.getSessionId();

            chatSubscribeService.removeChatSubscription(userId, sessionId);
            log.info("[WS Disconnect] User: {}, Session Id: {}, Session Count Decremented", userId, sessionId);
        } catch (Exception e) {
            log.error("[WS Disconnect] 데이터 처리 중 오류 발생: {}", e.getMessage());
        }
    }

    private void validateSessionAttributesAndUserId(Map<String, Object> sessionAttributes) {
        if (sessionAttributes == null) {
            throw new WebSocketConnectionException(ResponseStatus.SERVER_ERROR);
        }

        if (!sessionAttributes.containsKey("userId")) {
            throw new WebSocketConnectionException(ResponseStatus.INVALID_CHATTING_SESSION);
        }
    }
}
