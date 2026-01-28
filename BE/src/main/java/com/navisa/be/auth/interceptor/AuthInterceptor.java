package com.navisa.be.auth.interceptor;

import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.common.model.enums.ResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String token = authHeader.split(" ")[1];

        // 유효성 검증
        if (!jwtProvider.validateToken(token)) {
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmail(token);

        request.setAttribute("accessToken", token);
        request.setAttribute("email", email);

        return true;
    }
}
