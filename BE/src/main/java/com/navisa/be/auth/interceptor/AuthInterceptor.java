package com.navisa.be.auth.interceptor;

import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.global.web.response.ResponseStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.SignatureException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String[] parts = authHeader.split(" ");
        if (parts.length != 2) {
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String token = parts[1];

        // 유효성 검증
        try {
            jwtProvider.validateToken(token);
        } catch (ExpiredJwtException e) {
            log.warn("[인증] 액세스 토큰 만료됨 - 요청 URI: {}, 메시지: {}",
                    request.getRequestURI(), e.getMessage());
            throw new AuthException(ResponseStatus.ACCESS_TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            log.error("[인증] 보안 위반 - 잘못된 토큰 서명 또는 형식. 요청 URI: {}",
                    request.getRequestURI());
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        } catch (Exception e) {
            // 그 외 기타 예외 상황
            log.error("[인증] 토큰 검증 중 알 수 없는 에러 발생: {} ({})",
                    e.getClass().getSimpleName(), e.getMessage());
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmail(token);

        request.setAttribute("accessToken", token);
        request.setAttribute("email", email);

        return true;
    }
}
