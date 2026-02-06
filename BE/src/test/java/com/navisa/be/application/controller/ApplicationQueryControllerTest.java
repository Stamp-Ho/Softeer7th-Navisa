package com.navisa.be.application.controller;

import com.navisa.be.application.controller.ApplicationQueryController;
import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.service.ApplicationQueryService;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
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
    private AuthService authService;

    @MockitoBean
    private ApplicationQueryService applicationQueryService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private UserRepository userRepository;

    @DisplayName("로그인한 행정사는 자신이 최근에 수정한 비자 신청서 목록을 조회할 수 있다.")
    @Test
    void getRecentVisaForms() throws Exception {
        // given
        String email = "agent@navisa.com";
        String token = "accessToken";

        given(jwtProvider.validateToken(token)).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(email);

        RecentVisaFormsResponse summary = new RecentVisaFormsResponse(
                UUID.randomUUID(),
                "Nick Judy Elizabeth Maya",
                false,
                105,
                "https://s3.com/profiles/fox.png",
                "2025. 06. 21"
        );

        List<RecentVisaFormsResponse> response = List.of(summary);

        given(applicationQueryService.getRecentVisaForms(anyString()))
                .willReturn(response);

        // when
        ResultActions actions = mockMvc.perform(
                get("/api/visa-forms/recent-applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result[0].title").value("Nick Judy Elizabeth Maya"))
                .andExpect(jsonPath("$.result[0].currentStep").value(105))
                .andExpect(jsonPath("$.result[0].isDone").value(false))
                .andExpect(jsonPath("$.result[0].lastModifiedAt").value("2025. 06. 21"));
    }
}