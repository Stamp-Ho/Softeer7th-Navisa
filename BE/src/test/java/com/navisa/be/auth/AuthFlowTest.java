package com.navisa.be.auth;

import com.navisa.be.auth.dto.request.LoginRequest;
import com.navisa.be.auth.dto.response.LoginResponse;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.common.annotation.LoginUser;
import com.navisa.be.common.dto.response.BaseResponse;
import com.navisa.be.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@Import(AuthFlowTest.TestUserController.class)
class AuthFlowTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("실제 로그인 API를 호출하여 컨트롤러를 테스트한다")
    void login_controller_test() throws Exception {
        LoginRequest request = new LoginRequest("test@test.com", "password123");

        given(authService.login(any())).willReturn(new LoginResponse("at", "rt", java.util.UUID.randomUUID()));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // --- 테스트용 가짜 컨트롤러 ---
    @RestController
    static class TestUserController {
        @GetMapping("/api/test/me")
        public BaseResponse<String> getMyInfo(@LoginUser String email) {
            return new BaseResponse<>(email + "님 인증 성공");
        }
    }

    @Test
    @DisplayName("유효한 토큰으로 접근 시 인증에 성공해야 한다")
    void auth_success_test() throws Exception {
        String email = "test@navisa.com";
        String token = jwtProvider.createAccessToken(email);

        mockMvc.perform(get("/api/test/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 없이 인증이 필요한 API 호출 시 401을 반환해야 한다")
    void auth_fail_test() throws Exception {
        mockMvc.perform(get("/api/test/me"))
                .andExpect(status().isUnauthorized());
    }
}