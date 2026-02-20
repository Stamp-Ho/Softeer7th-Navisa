package com.navisa.be.chat.interceptor;

import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.chat.exception.WebSocketConnectionException;
import com.navisa.be.chat.model.entity.ChatUserPrincipal;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

            if (token == null || !token.startsWith("Bearer ")) {
                log.error("[WS Auth] missing token");
                throw new WebSocketConnectionException(ResponseStatus.INVALID_TOKEN);
            }

            token = token.substring(7);

            log.debug("token : {}", token.substring(0, 7));

            // 토큰 존재 여부 및 유효성 검사
            try {
                // getClaims 하나로 검증과 데이터 추출을 동시에 완료 (60초 유예 적용)
                Claims claims = jwtProvider.getClaims(token);
                String email = claims.getSubject();

                // 세션 속성 확인
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes == null) {
                    throw new WebSocketConnectionException(ResponseStatus.BAD_REQUEST);
                }

                // 사용자 조회 및 권한 체크
                User findUser = userCrudService.findByEmail(email);
                if (!findUser.getUserType().equals(UserType.FILLED_FOREIGNER)
                        && !findUser.getUserType().equals(UserType.VALID_AGENT)) {
                    throw new WebSocketConnectionException(ResponseStatus.INVALID_USER);
                }

                // Principal 설정 및 세션 저장
                Principal principal = new ChatUserPrincipal(findUser.getId().toString());
                accessor.setUser(principal);
                sessionAttributes.put("userId", findUser.getId().toString());

                log.info("[WS Auth] 인증 성공 - User: {}, ID: {}", email, findUser.getId());
            } catch (WebSocketConnectionException e) {
                throw e;
            } catch (ExpiredJwtException e) {
                log.warn("[WS Auth] 토큰 만료됨 - URI: /ws, Message: {}", e.getMessage());
                throw new WebSocketConnectionException(ResponseStatus.ACCESS_TOKEN_EXPIRED);
            } catch (Exception e) {
                // 그 외 모든 예외 (서명 불일치, 형식 오류 등)
                log.error("[WS Auth] 인증 실패 - 타입: {}, 사유: {}", e.getClass().getSimpleName(), e.getMessage());
                throw new WebSocketConnectionException(ResponseStatus.INVALID_TOKEN);
            }
        }

        return message;
    }
}
