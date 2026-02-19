package com.navisa.be.chat.interceptor;

import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.chat.model.entity.ChatUserPrincipal;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final UserCrudService userCrudService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        log.info("[WS Auth] 진입");

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null)
            throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);

        if (accessor.getCommand().equals(StompCommand.CONNECT)) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if(token == null || !token.startsWith("Bearer ")){
                log.error("[WS Auth] missing token");
                throw new WebSocketConnectionException(ResponseStatus.INVALID_TOKEN);
            }

            token = token.substring(7);

            log.debug("token : {}", token.substring(0, 7));

            // 토큰 존재 여부 및 유효성 검사
            if (!jwtProvider.validateToken(token)) {
                log.error("[WS Auth] Invalid token");
                throw new WebSocketConnectionException(ResponseStatus.INVALID_TOKEN);
            }

            // 토큰에서 이메일(또는 식별자) 추출
            String email = jwtProvider.getEmail(token);

            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            if (sessionAttributes == null)
                throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);

            User findUser = userCrudService.findByEmail(email);

            if (!findUser.getUserType().equals(UserType.FILLED_FOREIGNER)
                    && !findUser.getUserType().equals(UserType.VALID_AGENT))
                throw new WebSocketConnectionException(ResponseStatus.INVALID_USER);

            // Redis Pub/Sub으로 user별 listening로직을 구현하기 위한 principal
            Principal principal = new ChatUserPrincipal(findUser.getId().toString());
            accessor.setUser(principal);

            // 세션 속성(SessionAttributes)에 저장하여 Disconnect 시점까지 활용
            log.info("userId : {}", findUser.getId());

            sessionAttributes.put("userId", findUser.getId().toString());

            log.info("[WS Auth] Success for user: {}", email);
        }

        return message;
    }
}
