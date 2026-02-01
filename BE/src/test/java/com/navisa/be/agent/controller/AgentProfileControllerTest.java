package com.navisa.be.agent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.dto.request.RegisterAgentProfileRequest;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileService;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.support.AgentFixture;
import com.navisa.be.support.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc // MockMvc를 사용할 수 있게 설정
@Transactional // 테스트 후 데이터베이스 롤백
class AgentProfileControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AgentProfileService agentProfileService; // 실제 서비스 대신 Mock 사용
    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("에이전트 프로필 등록 성공 테스트")
    void registerAgentProfileSuccess() throws Exception {
        // given
        RegisterAgentProfileRequest request = AgentFixture.getRegisterAgentProfileRequest();

        when(agentProfileService.registerAgentProfile(any(RegisterAgentProfileCommand.class))).thenReturn(mock(AgentProfile.class));

        String accessToken = jwtProvider.createAccessToken("email");

        // when & then
        mockMvc.perform(post("/api/agent/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print()); // 요청 응답 로그 출력
    }

    @Test
    @DisplayName("에이전트 프로필 등록 요청의 필드가 null이면 실패한다")
    void registerAgentProfileFail_whenRequestConsistOfNull() throws Exception {
        // given
        RegisterAgentProfileRequest request = AgentFixture.getRegisterAgentProfileRequestConsistingOfNull();

        when(agentProfileService.registerAgentProfile(any(RegisterAgentProfileCommand.class))).thenReturn(mock(AgentProfile.class));

        String accessToken = jwtProvider.createAccessToken("email");

        // when & then
        mockMvc.perform(post("/api/agent/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("검증 실패")))
                .andDo(print()); // 요청 응답 로그 출력
    }
}