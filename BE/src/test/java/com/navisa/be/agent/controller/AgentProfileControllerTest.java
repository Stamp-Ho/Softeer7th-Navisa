package com.navisa.be.agent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.agent.dto.request.AgentProfileUpdateRequest;
import com.navisa.be.agent.dto.response.AgentDetailResponse;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileRegistrationService;
import com.navisa.be.agent.service.AgentProfileSearchService;
import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.global.common.service.JobCodeService;
import com.navisa.be.global.web.resolver.LoginUserResolver;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentProfileController.class)
class AgentProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AgentProfileRegistrationService agentProfileRegistrationService;

    @MockitoBean
    private AgentProfileSearchService agentProfileSearchService;

    @MockitoBean
    private JobCodeService jobCodeService;

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

    @DisplayName("행정사 프로필 수정 시 200 OK와 수정된 정보를 반환한다.")
    @Test
    void updateAgentProfile_Success() throws Exception {
        // given
        String email = "agent@navisa.com";
        AgentProfileUpdateRequest request = new AgentProfileUpdateRequest(
                "new-key", "010-1234-5678", "내비자 사무소", null, null,
                "나비빌딩 501호", null, null, null, null
        );
        AgentDetailResponse response = createMockResponse();

        given(agentProfileRegistrationService.updateAgentProfile(any(AgentProfileUpdateRequest.class), eq(email)))
                .willReturn(response);

        mockInterceptorAndResolver(email);

        // when & then
        mockMvc.perform(patch("/api/agent/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.officeInfo.officeAddressDetail").value("나비빌딩 501호"))
                .andExpect(jsonPath("$.result.agentInfo.name").value("내비자"));
    }

    @DisplayName("일부 필드만 담긴 요청에도 200 OK와 수정된 정보를 반환한다.")
    @Test
    void updateAgentProfile_Partial_Success() throws Exception {
        // given
        String email = "agent@navisa.com";
        AgentProfileUpdateRequest request = new AgentProfileUpdateRequest(
                null, "010-0000-0000", null, null, null,
                null, null, null, null, null
        );

        AgentDetailResponse response = createMockResponse();

        given(agentProfileRegistrationService.updateAgentProfile(any(), eq(email)))
                .willReturn(response);

        mockInterceptorAndResolver(email);

        // when & then
        mockMvc.perform(patch("/api/agent/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void mockInterceptorAndResolver(String email) throws Exception {
        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(userTypeCheckInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(loginUserResolver.supportsParameter(any())).willReturn(true);
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);
    }

    private AgentDetailResponse createMockResponse() {
        AgentProfile mockProfile = mock(AgentProfile.class);

        given(mockProfile.getId()).willReturn(UUID.randomUUID());
        given(mockProfile.getName()).willReturn("내비자");
        given(mockProfile.getOfficeName()).willReturn("내비자 사무소");
        given(mockProfile.getOfficeAddress()).willReturn("서울시 강남구");
        given(mockProfile.getOfficeAddressDetail()).willReturn("나비빌딩 501호");
        given(mockProfile.getBusinessTime()).willReturn("09:00 - 18:00");
        given(mockProfile.getPhoneNumber()).willReturn("010-1111-2222");
        given(mockProfile.getComment()).willReturn("전문 행정사입니다.");
        given(mockProfile.getAdditionalHistory()).willReturn("이력사항 없음");

        return AgentDetailResponse.entityToDto(
                mockProfile,
                List.of(),
                "https://image.com/profile",
                Optional.empty(),
                0L
        );
    }
}
