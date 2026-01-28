package com.navisa.be.auth.service;

import com.navisa.be.auth.dto.*;
import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.model.entity.RefreshToken;
import com.navisa.be.auth.repository.RefreshTokenRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 구글 로그인
    @Transactional
    public LoginResponse googleLogin(GoogleLoginRequest request) {
        String email = googleOAuthService.getGoogleEmail(request.idToken());
        User existingUser = userRepository.findByEmail(email).orElse(null);

        if (existingUser != null && existingUser.getLoginType() == LoginType.EMAIL) {
            throw new AuthException(ResponseStatus.DUPLICATE_LOGIN_TYPE);
        }

        User user = (existingUser != null) ? existingUser :
                userRepository.save(User.createGoogleUser(email, request.userType()));

        user.updateLastLogin();

        String accessToken = jwtProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());
        refreshTokenRepository.save(new RefreshToken(user.getEmail(), refreshToken));

        return new LoginResponse(accessToken, refreshToken, user.getId());
    }

    // 일반 회원가입
    @Transactional
    public SignupResponse signup(SignupRequest request) {
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

        String accessToken = jwtProvider.createAccessToken(savedUser.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(savedUser.getEmail());
        refreshTokenRepository.save(new RefreshToken(savedUser.getEmail(), refreshToken));

        return new SignupResponse(accessToken, refreshToken, savedUser.getId(), savedUser.getUserType());
    }

    // 일반 로그인
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException(ResponseStatus.INVALID_USER));

        if (!BCrypt.checkpw(request.password(), user.getPasswordHash())) {
            throw new AuthException(ResponseStatus.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());
        refreshTokenRepository.save(new RefreshToken(user.getEmail(), refreshToken));

        return new LoginResponse(accessToken, refreshToken, user.getId());
    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken, LogoutRequest request) {

        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
             throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }
        String email = jwtProvider.getEmail(accessToken.substring(7));// "Bearer " 제거

        userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ResponseStatus.INVALID_USER));

        refreshTokenRepository.deleteById(email);
    }

    // 토큰 재발급
    public TokenResponse reissue(String refreshTokenValue) {
        // Refresh Token 유효성 검증
        if (!jwtProvider.validateToken(refreshTokenValue)) {
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String email = jwtProvider.getEmail(refreshTokenValue);

        RefreshToken savedToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new AuthException(ResponseStatus.INVALID_TOKEN));

        if (!savedToken.getToken().equals(refreshTokenValue)) {
            refreshTokenRepository.deleteById(email);
            throw new AuthException(ResponseStatus.INVALID_TOKEN);
        }

        String newAccessToken = jwtProvider.createAccessToken(email);
        String newRefreshToken = jwtProvider.createRefreshToken(email);

        refreshTokenRepository.save(new RefreshToken(email, newRefreshToken));

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    // 유저타입 검증
    private void validateInitialUserType(UserType userType) {
        // 가입 시에는 미인증 행정사(UNVALID_AGENT) 또는 미입력 외국인(UNFILLED_FOREIGNER)만 허용
        if (userType != UserType.UNVALID_AGENT && userType != UserType.UNFILLED_FOREIGNER) {
            throw new AuthException(ResponseStatus.INVALID_INITIAL_USER_TYPE);
        }
    }
}
