package com.navisa.be.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.common.model.enums.ResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketExceptionHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    /**
     * 클라이언트 메시지 처리 중 발생한 에러를 가로채어 ERROR 프레임을 생성합니다.
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        Throwable cause = ex;
        // MessageDeliveryException 등으로 감싸져 있는 경우 실제 원인을 찾음
        if (ex instanceof MessageDeliveryException && ex.getCause() != null) {
            cause = ex.getCause();
        }

        if (cause instanceof BaseException baseEx) {
            return prepareErrorMessage(baseEx.status, baseEx.getMessage());
        }

        log.error("WebSocket Unhandled Exception: ", ex);
        return prepareErrorMessage(ResponseStatus.SERVER_ERROR, "서버 내부 에러가 발생했습니다.");
    }

    /**
     * 최종적인 ERROR 프레임을 생성합니다.
     */
    private Message<byte[]> prepareErrorMessage(ResponseStatus status, String message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        accessor.setMessage(message); // 헤더의 message 필드에 에러 메시지 설정
        accessor.setLeaveMutable(true);

        BaseResponse<Void> response = new BaseResponse<>(status, message);
        String payload = "";

        try {
            payload = objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            log.error("JSON Serialization Error", e);
        }

        return MessageBuilder.createMessage(
                payload.getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders()
        );
    }
}
