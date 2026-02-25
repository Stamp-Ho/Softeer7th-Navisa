package com.navisa.be.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.application.dto.request.ApplicationFormFinishedStatusRequest;
import com.navisa.be.application.dto.request.ApplicationFormSectionDataRequest;
import com.navisa.be.application.dto.response.ApplicationFormDetailResponse;
import com.navisa.be.application.dto.response.ApplicationFormFinishedStatusResponse;
import com.navisa.be.application.dto.response.ApplicationFormIdResponse;
import com.navisa.be.application.dto.response.RecentApplicationFormsResponse;
import com.navisa.be.application.service.ApplicationFormForForeignerService;
import com.navisa.be.application.service.ApplicationFormForAgentService;
import com.navisa.be.application.service.ApplicationFormRegistrationService;
import com.navisa.be.application.service.ApplicationFormSearchService;
import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.global.web.resolver.LoginUserResolver;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.navisa.be.global.web.annotation.LoginUser;

@WebMvcTest(ApplicationFormController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationFormControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    private ApplicationFormForForeignerService applicationFormForForeignerService;

    @MockitoBean
    private ApplicationFormForAgentService applicationFormForAgentService;

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
    @MockitoBean
    private ApplicationFormRegistrationService applicationFormRegistrationService;

    @MockitoBean
    private ApplicationFormSearchService applicationFormSearchService;

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
        given(loginUserResolver.supportsParameter(argThat(p -> p.hasParameterAnnotation(LoginUser.class))))
                .willReturn(true);
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
                "fields", List.of(Map.of("fieldId", 101, "value", "Hong Gil Dong")));
        ApplicationFormSectionDataRequest request = new ApplicationFormSectionDataRequest(150, 10,
                List.of(section1));
        ApplicationFormIdResponse response = new ApplicationFormIdResponse(visaFormId, updatedAt);

        given(applicationFormRegistrationService.saveApplicationForm(eq(email), eq(visaFormId),
                any(ApplicationFormSectionDataRequest.class)))
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

        ApplicationFormIdResponse response = new ApplicationFormIdResponse(visaFormId, now);
        given(applicationFormRegistrationService.saveProfilePhoto(eq(email), eq(visaFormId), eq(objectKey)))
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

        ApplicationFormIdResponse response = new ApplicationFormIdResponse(visaFormId, now);
        given(applicationFormForAgentService.updateApplicationStatus(eq(email), eq(visaFormId), eq(true)))
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

    @Test
    @DisplayName("PATCH /api/application-forms/{formId}/status/finished 호출 시 성공 응답을 반환한다.")
    void finishApplication_Controller_Success() throws Exception {
        // given
        String email = "agent@navisa.com";
        UUID formId = UUID.randomUUID();
        UUID newFormId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        ApplicationFormFinishedStatusResponse response = new ApplicationFormFinishedStatusResponse(formId, newFormId, now);
        given(applicationFormForAgentService.finishApplication(eq(email), eq(formId), eq(true)))
                .willReturn(response);

        ApplicationFormFinishedStatusRequest request = new ApplicationFormFinishedStatusRequest(true);

        // when & then
        mockMvc.perform(patch("/api/application-forms/{formId}/status/finished", formId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.closedVisaFormId").value(formId.toString()))
                .andExpect(jsonPath("$.result.newVisaFormId").value(newFormId.toString()));
    }

    @Test
    @DisplayName("외국인이 강제 종료 API를 호출하면 200 OK와 갱신된 폼 정보를 반환한다.")
    void finishByForeigner_Controller_Success() throws Exception {
        // given
        String email = "foreigner@navisa.com";
        UUID oldFormId = UUID.randomUUID();
        UUID newFormId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        ApplicationFormFinishedStatusResponse response = new ApplicationFormFinishedStatusResponse(oldFormId, newFormId, now);

        given(applicationFormForForeignerService.finishByForeigner(eq(email)))
                .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/application-forms/status/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.closedVisaFormId").value(oldFormId.toString()))
                .andExpect(jsonPath("$.result.newVisaFormId").value(newFormId.toString()));
    }

    @Test
    @DisplayName("로그인한 행정사는 최근 수정한 비자 신청서 목록을 조회할 수 있다.")
    void getRecentVisaForms() throws Exception {
        // given
        String email = "agent@navisa.com";
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        RecentApplicationFormsResponse summary = new RecentApplicationFormsResponse(
                UUID.randomUUID(), "Nick Judy", false, 0, 91, "img.png", LocalDateTime.now());
        given(applicationFormSearchService.getRecentApplicationForms(email)).willReturn(List.of(summary));

        // when & then
        mockMvc.perform(get("/api/application-forms/recent-applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].title").value("Nick Judy"))
                .andExpect(jsonPath("$.result[0].currentStep").value(0))
                .andExpect(jsonPath("$.result[0].totalCount").value(91));
    }

    @Test
    @DisplayName("로그인한 행정사가 처음 생성된 신청서를 조회하면 초기값(0, 91)을 반환한다.")
    void getRecentVisaForms_InitialValues() throws Exception {
        // given
        String email = "agent@navisa.com";
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        RecentApplicationFormsResponse initialSummary = new RecentApplicationFormsResponse(
                UUID.randomUUID(), "New Foreigner", false, 0, 91, "default.png", LocalDateTime.now());

        given(applicationFormSearchService.getRecentApplicationForms(email))
                .willReturn(List.of(initialSummary));

        // when & then
        mockMvc.perform(get("/api/application-forms/recent-applications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].currentStep").value(0))
                .andExpect(jsonPath("$.result[0].totalCount").value(91));
    }

    @Test
    @DisplayName("외국인이 본인의 신규 신청서를 조회하면 진행도 초기값(0, 91)이 포함되어야 한다.")
    void getLatestVisaForm_InitialProgress() throws Exception {
        // given
        String email = "foreigner@navisa.com";
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        ApplicationFormDetailResponse initialDetail = new ApplicationFormDetailResponse(
                UUID.randomUUID(), "img.png", false, LocalDateTime.now(),
                91, // totalCount
                0,  // currentStep
                List.of(Collections.emptyMap()), 1L);

        given(applicationFormSearchService.getLatestApplicationFormForForeigner(email))
                .willReturn(initialDetail);

        // when & then
        mockMvc.perform(get("/api/application-forms/foreigner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalCount").value(91))
                .andExpect(jsonPath("$.result.filledCount").value(0));
    }

    @Test
    @DisplayName("외국인 최신 폼 조회 시 200을 반환한다.")
    void getLatestVisaForm_Returns200() throws Exception {
        // given
        String email = "foreigner@navisa.com";
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        ApplicationFormDetailResponse response = new ApplicationFormDetailResponse(
                UUID.randomUUID(), "img.png", false, LocalDateTime.now(), 150, 10,
                List.of(Collections.emptyMap()), 1L);
        given(applicationFormSearchService.getLatestApplicationFormForForeigner(email)).willReturn(response);

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

        ApplicationFormDetailResponse response = new ApplicationFormDetailResponse(
                formId, "img.png", false, LocalDateTime.now(), 150, 80,
                List.of(Collections.emptyMap()), 1L);
        given(applicationFormSearchService.getApplicationFormForAgent(eq(email), eq(formId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/application-forms/agent/{formId}", formId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.applicationFormId").value(formId.toString()));
    }
}
