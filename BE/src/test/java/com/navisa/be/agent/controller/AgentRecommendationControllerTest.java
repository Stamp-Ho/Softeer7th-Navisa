package com.navisa.be.agent.controller;

import com.navisa.be.agent.service.AgentRecommendationService;
import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.global.web.resolver.LoginUserResolver;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentRecommendationController.class)
class AgentRecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgentRecommendationService agentRecommendationService;

    @MockitoBean
    private ForeignerQueryService foreignerQueryService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @MockitoBean
    private UserTypeCheckInterceptor userTypeCheckInterceptor;

    @MockitoBean
    private LoginUserResolver loginUserResolver;

    @Test
    @DisplayName("추천 API 호출 시 정상적으로 리스트를 반환한다")
    void getPersonalizedAgents_Success() throws Exception {
        // given
        String mockToken = "Bearer test-access-token";
        String mockEmail = "test@navisa.com";
        UUID foreignerId = UUID.randomUUID();

        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(userTypeCheckInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);
        given(foreignerQueryService.getForeignerIdByEmail(mockEmail)).willReturn(foreignerId);
        given(agentRecommendationService.getPersonalizedAgents(foreignerId)).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/home/user/agent")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result").isArray());
    }
}
