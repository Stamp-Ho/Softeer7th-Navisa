package com.navisa.be.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.dto.request.VisaApplicationFinishRequest;
import com.navisa.be.application.dto.request.VisaApplicationSaveRequest;
import com.navisa.be.application.dto.response.VisaApplicationFinishResponse;
import com.navisa.be.application.dto.response.VisaApplicationSaveResponse;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.application.service.ApplicationCommandService;
import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.entity.Proposal;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.common.resolver.LoginUserResolver;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Test
    @DisplayName("PATCH /api/application-forms/{formId}/status/finished 호출 시 성공 응답을 반환한다.")
    void finishApplication_Controller_Success() throws Exception {
        // given
        String email = "agent@navisa.com";
        UUID formId = UUID.randomUUID();
        UUID newFormId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(email);

        VisaApplicationFinishResponse response = new VisaApplicationFinishResponse(formId, newFormId, now);
        given(applicationCommandService.finishApplication(eq(email), eq(formId), eq(true)))
                .willReturn(response);

        VisaApplicationFinishRequest request = new VisaApplicationFinishRequest(true);

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

        VisaApplicationFinishResponse response = new VisaApplicationFinishResponse(oldFormId, newFormId, now);

        given(applicationCommandService.finishByForeigner(eq(email)))
                .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/application-forms/status/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.closedVisaFormId").value(oldFormId.toString()))
                .andExpect(jsonPath("$.result.newVisaFormId").value(newFormId.toString()));
    }
}
