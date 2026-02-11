package com.navisa.be.application.controller;

import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.dto.response.VisaApplicationDetailResponse;
import com.navisa.be.application.service.ApplicationQueryService;
import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.common.resolver.LoginUserResolver;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationQueryControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    private ApplicationQueryService applicationQueryService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @MockitoBean
    private UserTypeCheckInterceptor userTypeCheckInterceptor;

    @MockitoBean
    private LoginUserResolver loginUserResolver;

    @TestConfiguration
    static class TestConfig implements WebMvcConfigurer {
        @Autowired
        private LoginUserResolver loginUserResolver;

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(loginUserResolver);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(userTypeCheckInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(loginUserResolver.supportsParameter(any())).willReturn(true);
    }

    @Test
    @DisplayName("로그인한 행정사는 최근 수정한 비자 신청서 목록을 조회할 수 있다.")
    void getRecentVisaForms() throws Exception {
        // given
        String email = "agent@navisa.com";
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        RecentVisaFormsResponse summary = new RecentVisaFormsResponse(
                UUID.randomUUID(), "Nick Judy", false, 105, "img.png", LocalDateTime.now()
        );
        given(applicationQueryService.getRecentVisaForms(email)).willReturn(List.of(summary));

        // when & then
        mockMvc.perform(get("/api/application-forms/recent-applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].title").value("Nick Judy"));
    }

    @Test
    @DisplayName("외국인 최신 폼 조회 시 200을 반환한다.")
    void getLatestVisaForm_Returns200() throws Exception {
        // given
        String email = "foreigner@navisa.com";
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        VisaApplicationDetailResponse response = new VisaApplicationDetailResponse(
                UUID.randomUUID(), "img.png", false, LocalDateTime.now(), 150, 10, List.of(Collections.emptyMap())
        );
        given(applicationQueryService.getLatestVisaFormForForeigner(email)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/application-forms/foreigner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("행정사는 특정 신청서 ID로 상세 정보를 조회할 수 있다.")
    void getVisaFormForAgent_Success() throws Exception {
        // given
        String email = "agent@navisa.com";
        UUID formId = UUID.randomUUID();
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        VisaApplicationDetailResponse response = new VisaApplicationDetailResponse(
                formId, "img.png", false, LocalDateTime.now(), 150, 80, List.of(Collections.emptyMap())
        );
        given(applicationQueryService.getVisaFormForAgent(eq(email), eq(formId))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/application-forms/agent/{formId}", formId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.applicationFormId").value(formId.toString()));
    }
}
