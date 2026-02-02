package com.navisa.be.agent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.dto.request.RegisterAgentProfileRequest;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileCommandService;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.repository.JobCodeRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc // MockMvc를 사용할 수 있게 설정
@Transactional // 테스트 후 데이터베이스 롤백
class AgentProfileIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AgentProfileCommandService agentProfileCommandService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Test
    @DisplayName("에이전트 프로필 등록 성공에 성공한다")
    void registerAgentProfile_shouldReturnOk() throws Exception {
        // given
        RegisterAgentProfileRequest request = AgentFixture.getRegisterAgentProfileRequest();

        when(agentProfileCommandService.registerAgentProfile(any(RegisterAgentProfileCommand.class)))
                .thenReturn(mock(AgentProfile.class));

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
    @DisplayName("에이전트 프로필 등록 요청의 필드가 null이면 400에러를 반환한다")
    void registerAgentProfile_shouldReturnBadRequest_whenRequestConsistOfNull() throws Exception {
        // given
        RegisterAgentProfileRequest request = AgentFixture.getRegisterAgentProfileRequestConsistingOfNull();

        when(agentProfileCommandService.registerAgentProfile(any(RegisterAgentProfileCommand.class)))
                .thenReturn(mock(AgentProfile.class));

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

    @Test
    @DisplayName("직무코드 조회에 성공한다")
    void getJobCode_shouldReturnJobCodeList() throws Exception {
        // given
        jobCodeRepository.save(new JobCode(null, "C001", "백엔드 개발자", new float[512]));
        jobCodeRepository.save(new JobCode(null, "C002", "프론트엔드 개발자", new float[512]));

        String accessToken = jwtProvider.createAccessToken("email");

        // when & then
        mockMvc.perform(get("/api/agent/register-form/jobcodes")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.jobCodeList").isArray())
                .andExpect(jsonPath("$.result.jobCodeList[0].name").value("백엔드 개발자"))
                .andDo(print());
    }
}