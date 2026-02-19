package com.navisa.be.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.auth.controller.AuthController;
import com.navisa.be.auth.dto.request.LoginRequest;
import com.navisa.be.auth.dto.request.SignupRequest;
import com.navisa.be.auth.dto.response.LoginResponse;
import com.navisa.be.auth.dto.response.SignupResponse;
import com.navisa.be.auth.dto.response.TokenResponse;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.user.model.enums.UserType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("회원가입 API: 성공 시 200 코드와 유저 정보를 반환한다")
    void signup_success() throws Exception {
        // given
        UUID mockUserId = UUID.fromString("3e00d7d2-8f70-4992-aee2-2e347dce42d4");

        SignupRequest request = new SignupRequest("test@test.com", "password123", UserType.INVALID_AGENT);
        SignupResponse responseDto = new SignupResponse("access-token", mockUserId, UserType.INVALID_AGENT);

        given(authService.signup(any(SignupRequest.class), any(HttpServletResponse.class)))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.accessToken").value("access-token"))
                .andExpect(jsonPath("$.result.userId").value(mockUserId.toString()))
                .andExpect(jsonPath("$.result.userType").value("INVALID_AGENT"));
    }

    @Test
    @DisplayName("로그인 API: 성공 시 200 코드와 액세스 토큰을 반환하고, 쿠키에 리프레시 토큰을 반환한다.")
    void login_success() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@test.com", "password123");
        LoginResponse responseDto = new LoginResponse("access-token", UUID.randomUUID(), UserType.INVALID_AGENT);

        willAnswer(invocation -> {
            HttpServletResponse response = invocation.getArgument(1);
            response.addHeader(HttpHeaders.SET_COOKIE, "refreshToken=mock-refresh-token; Path=/; HttpOnly");
            return responseDto;
        }).given(authService).login(any(LoginRequest.class), any(HttpServletResponse.class));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accessToken").value("access-token"))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", Matchers.containsString("refreshToken")));
    }

    @Test
    @DisplayName("로그아웃 API: 성공 시 Set-Cookie 헤더를 통해 쿠키를 만료시킨다")
    void logout_success() throws Exception {
        //given
        willDoNothing().given(jwtProvider).validateToken(anyString());
        given(jwtProvider.getEmail(anyString())).willReturn("test@test.com");

        // when & then
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", Matchers.containsString("Max-Age=0")))
                .andExpect(header().string("Set-Cookie", Matchers.containsString("SameSite=None")));
    }

    @Test
    @DisplayName("토큰 재발급 API: 쿠키의 refreshToken을 읽어 새 액세스 토큰을 반환한다")
    void reissue_success() throws Exception {
        // given
        String refreshToken = "valid-refresh-token";
        TokenResponse responseDto = new TokenResponse("new-access-token");

        given(authService.reissue(eq(refreshToken), any(HttpServletResponse.class)))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/auth/reissue")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accessToken").value("new-access-token"));
    }
}