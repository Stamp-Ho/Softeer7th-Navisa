package com.navisa.be.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.application.dto.request.VisaApplicationSaveRequest;
import com.navisa.be.application.dto.response.VisaApplicationSaveResponse;
import com.navisa.be.application.service.ApplicationCommandService;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApplicationCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationCommandControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private ApplicationCommandService applicationCommandService;

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
    @DisplayName("비자 신청서 자동 저장 시 200 OK와 수정된 정보를 반환한다.")
    void saveVisaForm_Success() throws Exception {
        // given
        String email = "agent@navisa.com";
        UUID visaFormId = UUID.randomUUID();
        LocalDateTime updatedAt = LocalDateTime.of(2026, 2, 10, 12, 0, 0);

        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        Map<String, Object> section1 = Map.of(
                "sectionId", 1,
                "fields", List.of(Map.of("fieldId", 101, "value", "Hong Gil Dong"))
        );
        VisaApplicationSaveRequest request = new VisaApplicationSaveRequest(150, 10, List.of(section1));
        VisaApplicationSaveResponse response = new VisaApplicationSaveResponse(visaFormId, updatedAt);

        given(applicationCommandService.saveVisaForm(eq(email), eq(visaFormId), any(VisaApplicationSaveRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/application-forms/{formId}", visaFormId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.visaFormId").value(visaFormId.toString()))
                .andExpect(jsonPath("$.result.updatedAt").value("2026-02-10T12:00:00"));
    }

    @Test
    @DisplayName("증명사진 경로 저장 API 호출 시 200 OK를 반환한다.")
    void saveProfilePhoto_Controller_Success() throws Exception {
        // given
        String email = "agent@navisa.com";
        UUID visaFormId = UUID.randomUUID();
        String objectKey = "test/path/image.webp";
        LocalDateTime now = LocalDateTime.now();

        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        VisaApplicationSaveResponse response = new VisaApplicationSaveResponse(visaFormId, now);
        given(applicationCommandService.saveProfilePhoto(eq(email), eq(visaFormId), eq(objectKey)))
                .willReturn(response);

        Map<String, String> request = Map.of("profileObjectKey", objectKey);

        // when & then
        mockMvc.perform(post("/api/application-forms/{formId}/image", visaFormId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.visaFormId").value(visaFormId.toString()));
    }

    @Test
    @DisplayName("신청서 상태 변경 API 호출 시 200 OK와 수정 시각을 반환한다.")
    void updateApplicationStatus_Controller_Success() throws Exception {
        // given
        String email = "agent@navisa.com";
        UUID visaFormId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        VisaApplicationSaveResponse response = new VisaApplicationSaveResponse(visaFormId, now);
        given(applicationCommandService.updateApplicationStatus(eq(email), eq(visaFormId), eq(true)))
                .willReturn(response);

        Map<String, Boolean> request = Map.of("isDone", true);

        // when & then
        mockMvc.perform(patch("/api/application-forms/{formId}/status", visaFormId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.visaFormId").value(visaFormId.toString()))
                .andExpect(jsonPath("$.code").value(200));
    }
}
