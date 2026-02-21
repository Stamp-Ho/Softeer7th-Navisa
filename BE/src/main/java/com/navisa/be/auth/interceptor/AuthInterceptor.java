package com.navisa.be.auth.interceptor;

import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.global.web.response.ResponseStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer ")) {
            log.warn("[인증 실패] 헤더 누락 또는 형식 오류: {}", authHeader);
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String[] parts = authHeader.trim().split("\\s+");
        if (parts.length != 2) {
            log.warn("[인증 실패] 헤더 구조 이상: {}", authHeader);
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String token = parts[1];

        try {
            Claims claims = jwtProvider.getClaims(token);
            String email = claims.getSubject();
            String userType = claims.get("userType", String.class);

            // 리팩토링 내용 배포 전에 발행된 토큰에 대해서 토큰 재발급 유도를 하는 로직
            if (userType == null) {
                throw new AuthException(ResponseStatus.ACCESS_TOKEN_EXPIRED);
            }

            request.setAttribute("accessToken", token);
            request.setAttribute("email", email);
            request.setAttribute("userType", userType);
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("[인증] 액세스 토큰 만료됨 - URI: {}, 메시지: {}", request.getRequestURI(), e.getMessage());
            throw new AuthException(ResponseStatus.ACCESS_TOKEN_EXPIRED);
        } catch (Exception e) {
            log.error("[인증] 토큰 검증 중 에러 발생: {} ({}) - 토큰(앞7자): {}",
                    e.getClass().getSimpleName(), e.getMessage(),
                    token.length() > 7 ? token.substring(0, 7) + "..." : token);
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }
    }
}
