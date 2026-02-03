package com.navisa.be.auth;

import com.navisa.be.auth.dto.request.GoogleLoginRequest;
import com.navisa.be.auth.dto.request.LoginRequest;
import com.navisa.be.auth.dto.request.LogoutRequest;
import com.navisa.be.auth.dto.request.SignupRequest;
import com.navisa.be.auth.dto.response.LoginResponse;
import com.navisa.be.auth.dto.response.SignupResponse;
import com.navisa.be.auth.dto.response.TokenResponse;
import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.model.entity.RefreshToken;
import com.navisa.be.auth.repository.RefreshTokenRepository;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.auth.service.GoogleOAuthService;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private GoogleOAuthService googleOAuthService;

    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("회원가입 성공: 유저 정보가 저장되고 토큰이 발급된다")
    void signupSuccess() {

        SignupRequest request = new SignupRequest("test@test.com", "password123", UserType.UNFILLED_FOREIGNER);
        User user = new User(request.email(), "hashedPassword", request.userType(), LoginType.EMAIL, true);

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(user);
        given(jwtProvider.createAccessToken(anyString())).willReturn("access-token");
        given(jwtProvider.createRefreshToken(anyString())).willReturn("refresh-token");

        SignupResponse responseResult = authService.signup(request, response);

        assertAll(
                () -> assertThat(responseResult.accessToken()).isEqualTo("access-token"),
                () -> verify(userRepository, times(1)).save(any(User.class)),
                () -> verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class))
        );
    }

    @Test
    @DisplayName("로그인 성공: 비밀번호가 일치하면 토큰을 반환한다")
    void loginSuccess() {

        String email = "test@test.com";
        String rawPassword = "password123";
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        User user = new User(email, hashed, UserType.UNFILLED_FOREIGNER, LoginType.EMAIL, true);

        LoginRequest request = new LoginRequest(email, rawPassword);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(jwtProvider.createAccessToken(email)).willReturn("access-token");
        given(jwtProvider.createRefreshToken(email)).willReturn("refresh-token");

        LoginResponse responseResult = authService.login(request, response);

        assertThat(responseResult.accessToken()).isEqualTo("access-token");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 실패: 존재하지 않는 유저인 경우 INVALID_USER 예외가 발생한다")
    void loginFailUserNotFound() {

        LoginRequest request = new LoginRequest("none@test.com", "password123");
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request, response))
                .isInstanceOf(AuthException.class)
                .hasMessage(ResponseStatus.INVALID_USER.getMessage());
    }

    @Test
    @DisplayName("로그아웃 성공: Redis에서 리프레시 토큰이 삭제된다")
    void logoutSuccess() {

        String accessToken = "Bearer valid-token";
        String email = "test@test.com";
        LogoutRequest request = new LogoutRequest("refresh-token");

        given(jwtProvider.validateToken(anyString())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(email);

        authService.logout(accessToken);

        verify(refreshTokenRepository, times(1)).deleteById(email);
    }

    @Test
    @DisplayName("회원가입 시 이미 존재하는 이메일이면 409 에러를 던진다")
    void signup_fail_duplicate_email() {
        String email = "duplicate@navisa.com";
        User existingUser = new User(email, "hashed_pw", UserType.UNVALID_AGENT, LoginType.EMAIL, true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        SignupRequest request = new SignupRequest(email, "password123", UserType.UNVALID_AGENT);

        assertThatThrownBy(() -> authService.signup(request, response))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("status", ResponseStatus.ALREADY_EXIST_USER);
    }

    @Test
    @DisplayName("회원가입 시 초기 허용되지 않은 UserType이면 400 에러를 던진다")
    void signup_fail_invalid_user_type() {
        SignupRequest request = new SignupRequest("new@navisa.com", "password123", UserType.VALID_AGENT);

        assertThatThrownBy(() -> authService.signup(request, response))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("status", ResponseStatus.INVALID_INITIAL_USER_TYPE);
    }

    @Test
    @DisplayName("회원가입에 성공하면 토큰과 유저 정보를 반환한다")
    void signup_success() {
        String email = "newuser@navisa.com";
        SignupRequest request = new SignupRequest(email, "password123", UserType.UNVALID_AGENT);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        // NPE 방지: save 호출 시 인자로 받은 유저 객체를 그대로 반환하도록 설정
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(jwtProvider.createAccessToken(anyString())).thenReturn("test-access-token");
        when(jwtProvider.createRefreshToken(anyString())).thenReturn("test-refresh-token");

        SignupResponse responseResult = authService.signup(request, response);

        assertThat(responseResult.accessToken()).isNotBlank();
        assertThat(responseResult.userType()).isEqualTo(UserType.UNVALID_AGENT);
    }

    @Test
    @DisplayName("구글 로그인 시 이메일 계정이 이미 존재하면 409 에러를 던진다")
    void google_login_fail_duplicate_type() {
        String idToken = "google-id-token";
        String email = "test@navisa.com";
        User existingEmailUser = new User(email, "hashed_pw", UserType.UNVALID_AGENT, LoginType.EMAIL, true);

        when(googleOAuthService.getGoogleEmail(idToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingEmailUser));

        GoogleLoginRequest request = new GoogleLoginRequest(idToken, UserType.UNVALID_AGENT);

        assertThatThrownBy(() -> authService.googleLogin(request, response))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("status", ResponseStatus.DUPLICATE_LOGIN_TYPE);
    }

    @Test
    @DisplayName("일반 로그인 시 비밀번호가 틀리면 예외가 발생한다")
    void login_fail_invalid_password() {
        String email = "user@navisa.com";
        // BCrypt로 암호화된 비밀번호를 가진 유저 모킹
        String hashedPw = BCrypt.hashpw("correct-password", BCrypt.gensalt());
        User user = new User(email, hashedPw, UserType.UNVALID_AGENT, LoginType.EMAIL, true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        LoginRequest request = new LoginRequest(email, "wrong-password");

        assertThatThrownBy(() -> authService.login(request, response))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("status", ResponseStatus.INVALID_PASSWORD);
    }

    @Test
    @DisplayName("토큰 재발급 성공: 새로운 엑세스 토큰을 반환하고 쿠키를 갱신한다")
    void reissueSuccess() {
        // given
        String oldRefreshToken = "old-rt";
        String email = "test@test.com";
        RefreshToken savedToken = new RefreshToken(email, oldRefreshToken);

        given(jwtProvider.validateToken(oldRefreshToken)).willReturn(true);
        given(jwtProvider.getEmail(oldRefreshToken)).willReturn(email);
        given(refreshTokenRepository.findById(email)).willReturn(Optional.of(savedToken));
        given(jwtProvider.createAccessToken(email)).willReturn("new-at");
        given(jwtProvider.createRefreshToken(email)).willReturn("new-rt");

        // when
        TokenResponse responseResult = authService.reissue(oldRefreshToken, response);

        // then
        assertThat(responseResult.accessToken()).isEqualTo("new-at");
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
    }
}
