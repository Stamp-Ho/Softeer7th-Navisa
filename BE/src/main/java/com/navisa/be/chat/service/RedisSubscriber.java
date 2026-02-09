package com.navisa.be.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            // 1. Redis 채널명을 통해 유저 UUID 추출 (user:ch:{uuid})
            String channel = new String(message.getChannel());
            String userUuid = channel.split(":")[2];

            // 2. Redis에서 발행된 메시지 본문(Payload) 파싱
            // TODO:: 추후 메시지 구조를 FE와 협의해서 어떤 정보를 구조화(DTO)해서 보내야 하는지 정의해야 함.
            // TODO:: 이 부분은 #182 이슈에서 처리
            String publishMessage = new String(message.getBody());

            // 3. 특정 유저에게만 전송
            // Spring 내부 로직에 의해 WebSocketAuthInterceptor에서 등록한 Principal을 기반으로
            // 사용자 세션을 찾아 메세지를 전송
            messagingTemplate.convertAndSendToUser(userUuid, "/chat/subscribe", publishMessage);
        } catch (Exception e) {
            log.error("Failed to process Redis message. Channel: {}, Error: {}",
                    new String(message.getChannel()), e.getMessage(), e);
        }
    }
}
