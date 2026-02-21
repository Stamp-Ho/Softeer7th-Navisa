package com.navisa.be.auth.jwt;

import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.enums.UserType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;
    private final long accessTokenValidity = 1000L * 60 * 10; // 10분
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7; //  7일

    // 빈 초기화 시점에 문자열 키를 Key 객체로 변환
    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    public String createAccessToken(String email, UUID userId, UserType userType) {
        return createToken(email, userId, userType, accessTokenValidity);
    }

    // Refresh Token 생성
    public String createRefreshToken(String email, UUID userId, UserType userType) {
        return createToken(email, userId, userType, refreshTokenValidity);
    }

    // 공통 토큰 생성 로직
    private String createToken(String email, UUID userId, UserType userType, long validityTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityTime);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId.toString())
                .claim("userType", userType.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    // 토큰에서 이메일 추출
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .clockSkewSeconds(60)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("[JwtProvider] 토큰 만료 에러: {}", e.getMessage());
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("[JwtProvider] 유효하지 않은 토큰 에러 - 타입: {}, 사유: {}, 토큰값: {}",
                    e.getClass().getSimpleName(), e.getMessage(), token);
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }
    }
}
