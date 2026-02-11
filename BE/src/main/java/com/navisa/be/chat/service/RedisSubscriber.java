package com.navisa.be.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.chat.dto.message.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * Redis에서 메시지가 발행(Publish)되면 호출되는 메서드
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 발행된 메시지 본문 파싱
            byte[] body = message.getBody();
            ChatMessageResponse response = objectMapper.readValue(body, ChatMessageResponse.class);

            log.debug("redis로 부터 온 메시지 {}", response);

            String receiverId = response.receiverId().toString();

            // 특정 유저에게만 전송
            // Spring 내부 로직에 의해 WebSocketAuthInterceptor에서 등록한 Principal을 기반으로 사용자 세션을 찾아 메세지를 전송
            messagingTemplate.convertAndSendToUser(receiverId, "/chat/subscribe", response);
        } catch (Exception e) {
            log.error("Failed to process Redis message. Channel: {}, Error: {}", new String(message.getChannel()), e.getMessage(), e);
        }
    }
}
