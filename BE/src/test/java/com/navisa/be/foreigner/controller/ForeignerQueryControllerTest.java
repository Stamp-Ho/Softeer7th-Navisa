package com.navisa.be.foreigner.controller;

import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.service.ForeignerQueryService;
import com.navisa.be.foreigner.service.ForeignerServiceFacade;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForeignerQueryController.class)
class ForeignerQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForeignerServiceFacade foreignerServiceFacade;

    @MockitoBean
    private ForeignerQueryService foreignerQueryService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private com.navisa.be.common.resolver.LoginUserResolver loginUserResolver;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("외국인 상세 요건 상태 조회 성공 시 200 OK와 상태 정보를 반환한다.")
    void checkForeignerFilledStatus_Success() throws Exception {
        // given
        UUID profileId = UUID.randomUUID();
        ForeignerStatusResponse response = new ForeignerStatusResponse(profileId, true);
        String mockEmail = "test@navisa.com";

        given(jwtProvider.validateToken(anyString())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);

        given(loginUserResolver.supportsParameter(any())).willReturn(true);
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(mockEmail);

        given(foreignerQueryService.checkForeignerFilledStatus(anyString())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/foreigner/requirements")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.foreignerProfileId").value(profileId.toString()))
                .andExpect(jsonPath("$.result.isCompletedRecommendation").value(true));
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 요청 시 401 Unauthorized를 반환한다.")
    void checkForeignerFilledStatus_InvalidToken() throws Exception {
        // given
        given(jwtProvider.validateToken(anyString())).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/foreigner/requirements")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 정보를 조회할 경우 400 Bad Request를 반환한다.")
    void checkForeignerFilledStatus_NotFound() throws Exception {
        // given
        String mockEmail = "nonexistent@navisa.com";
        given(jwtProvider.validateToken(anyString())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);

        given(loginUserResolver.supportsParameter(any())).willReturn(true);
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(mockEmail);

        given(foreignerQueryService.checkForeignerFilledStatus(mockEmail))
                .willThrow(new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        // when & then
        mockMvc.perform(get("/api/foreigner/requirements")
                        .header("Authorization", "Bearer test-token"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ResponseStatus.INVALID_FOREIGNER.getMessage()));
    }
}
