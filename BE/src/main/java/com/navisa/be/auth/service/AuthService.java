package com.navisa.be.auth.service;

import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.auth.dto.request.GoogleLoginRequest;
import com.navisa.be.auth.dto.request.LoginRequest;
import com.navisa.be.auth.dto.request.SignupRequest;
import com.navisa.be.auth.dto.response.LoginResponse;
import com.navisa.be.auth.dto.response.SignupResponse;
import com.navisa.be.auth.dto.response.TokenResponse;
import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.model.entity.RefreshToken;
import com.navisa.be.auth.repository.RefreshTokenRepository;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AgentProfileCrudService agentProfileCrudService;
    private final ForeignerProfileCrudService foreignerProfileCrudService;

    // 구글 로그인
    @Transactional
    public LoginResponse googleLogin(GoogleLoginRequest request, HttpServletResponse response) {
        String email = googleOAuthService.getGoogleEmail(request.idToken());
        User existingUser = userRepository.findByEmail(email).orElse(null);

        if (existingUser != null && existingUser.getLoginType() == LoginType.EMAIL) {
            throw new AuthException(ResponseStatus.DUPLICATE_LOGIN_TYPE);
        }

        User user = (existingUser != null) ? existingUser :
                userRepository.save(User.createGoogleUser(email, request.userType()));

        syncAgentActivityIfPresent(user);

        String accessToken = jwtProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        saveRefreshTokenInCookie(user.getEmail(), refreshToken, response);
        return new LoginResponse(accessToken, user.getId(), user.getUserType());
    }

    // 일반 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest request, HttpServletResponse response) {
        // 기존 유저 존재 여부 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new AuthException(ResponseStatus.ALREADY_EXIST_USER);
        }

        //  UserType 유효성 검증
        validateInitialUserType(request.userType());

        // 신규 유저 생성 및 저장
        String encodedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        User newUser = new User(
                request.email(),
                encodedPassword,
                request.userType(),
                LoginType.EMAIL,
                true
        );
        User savedUser = userRepository.save(newUser);
        syncAgentActivityIfPresent(savedUser);

        String accessToken = jwtProvider.createAccessToken(savedUser.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(savedUser.getEmail());

        saveRefreshTokenInCookie(savedUser.getEmail(), refreshToken, response);
        return new SignupResponse(accessToken, savedUser.getId(), savedUser.getUserType());
    }

    // 일반 로그인
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException(ResponseStatus.INVALID_USER));

        if (!BCrypt.checkpw(request.password(), user.getPasswordHash())) {
            throw new AuthException(ResponseStatus.INVALID_PASSWORD);
        }

        syncAgentActivityIfPresent(user);

        String accessToken = jwtProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        saveRefreshTokenInCookie(user.getEmail(), refreshToken, response);
        return new LoginResponse(accessToken, user.getId(), user.getUserType());
    }

    // 로그아웃
    @Transactional
    public void logout(String email) {
        if (email != null) {
            refreshTokenRepository.deleteById(email);
        }
    }

    // 토큰 재발급
    public TokenResponse reissue(String refreshTokenValue, HttpServletResponse response) {
        Claims claims;
        try {
            // 한 번의 파싱으로 검증과 데이터 확보 동시에 완료
            claims = jwtProvider.getClaims(refreshTokenValue);
        } catch (ExpiredJwtException e) {
            log.error("리프레시 토큰 만료됨: {}", e.getMessage());
            throw new AuthException(ResponseStatus.REFRESH_TOKEN_EXPIRED);
        }

        String email = claims.getSubject();

        RefreshToken savedToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AuthException(ResponseStatus.INVALID_TOKEN));

        if (!savedToken.getToken().equals(refreshTokenValue)) {
            refreshTokenRepository.deleteById(email);
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String newAccessToken = jwtProvider.createAccessToken(email);
        String newRefreshToken = jwtProvider.createRefreshToken(email);

        saveRefreshTokenInCookie(email, newRefreshToken, response);

        return new TokenResponse(newAccessToken);
    }

    private void saveRefreshTokenInCookie(String email, String refreshToken, HttpServletResponse response) {
        refreshTokenRepository.save(new RefreshToken(email, refreshToken));

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // HTTPS 환경 필수
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 유저타입 검증
    private void validateInitialUserType(UserType userType) {
        // 가입 시에는 미인증 행정사(INVALID_AGENT) 또는 미입력 외국인(UNFILLED_FOREIGNER)만 허용
        if (userType != UserType.INVALID_AGENT && userType != UserType.UNFILLED_FOREIGNER) {
            throw new AuthException(ResponseStatus.INVALID_INITIAL_USER_TYPE);
        }
    }

    public boolean checkUserType(String email, UserType[] userTypes) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ResponseStatus.FORBIDDEN));

        log.info("user.userType {}", user.getUserType());
        log.info("userTypes {}", Arrays.toString(userTypes));

        return Arrays.stream(userTypes)
                .anyMatch(userType -> userType.equals(user.getUserType()));
    }

    private void syncAgentActivityIfPresent(User user) {
        if (user.getUserType() == UserType.VALID_AGENT) {
            // 프로필이 존재하는 경우에만 활동 정보를 동기화
            if (agentProfileCrudService.existsByUserId(user.getId())) {
                agentProfileCrudService.syncAgentLoginActivity(user.getId());
            }
        }

        if (user.getUserType() == UserType.FILLED_FOREIGNER) {
            foreignerProfileCrudService.syncForeignerLoginActivity(user.getId());
        }
    }
}
