package com.navisa.be.chat.controller;

import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.chat.service.ChatSubscribeService;
import com.navisa.be.common.model.enums.ResponseStatus;
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

        // TODO:: 추후 캐싱 전략 도입시 아래 주석 부분을 활성화
        log.info("[WS Connect] New connection attempt");

        /*
         * StompHeaderAccessor headerAccessor =
         * StompHeaderAccessor.wrap(event.getMessage());
         *
         * if (headerAccessor.getSessionAttributes() == null) {
         * throw new BaseException(ResponseStatus.SERVER_ERROR);
         * }
         *
         * UUID userId = (UUID) headerAccessor.getSessionAttributes().get("userId");
         *
         * List<Long> subscribedRooms =
         * chatSubscribeService.subscribeUserAllRooms(userId);
         *
         * log.info("[WS Connect] User: {}, Rooms: {}", userId, subscribedRooms);
         */

        validateSessionAttributesAndUserId(sessionAttributes);

        String userIdStr = (String) sessionAttributes.get("userId");
        UUID userId = UUID.fromString(userIdStr);
        chatSubscribeService.increaseSessionCount(userId);
        log.info("[WS Connect] User: {}, Session Count Incremented", userId);
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        validateSessionAttributesAndUserId(sessionAttributes);

        if (destination == null || !destination.equals("/user/chat/subscribe")) {
            throw new WebSocketConnectionException(ResponseStatus.ILLEGAL_SUBSCRIBE_DESTINATION);
        }

        String userIdStr = (String) sessionAttributes.get("userId");
        UUID userId = UUID.fromString(userIdStr);
        chatSubscribeService.subscribeUserAllRooms(userId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        validateSessionAttributesAndUserId(sessionAttributes);

        String userIdStr = (String) sessionAttributes.get("userId");
        UUID userId = UUID.fromString(userIdStr);
        chatSubscribeService.decreaseSessionCount(userId);
        log.info("[WS Disconnect] User: {}, Session Count Decremented", userId);
    }

    private void validateSessionAttributesAndUserId(Map<String, Object> sessionAttributes) {
        if (sessionAttributes == null) {
            throw new WebSocketConnectionException(ResponseStatus.SERVER_ERROR);
        }

        if (!sessionAttributes.containsKey("userId")) {
            throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
        }
    }
}
