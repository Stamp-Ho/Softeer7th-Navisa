package com.navisa.be.foreigner.controller;

import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.foreigner.dto.request.ForeignerDetailRequest;
import com.navisa.be.foreigner.dto.response.ForeignerDetailResponse;
import com.navisa.be.foreigner.dto.response.ForeignerProgressResponse;
import com.navisa.be.foreigner.dto.response.ForeignerStatusResponse;
import com.navisa.be.foreigner.exception.ForeignerException;
import com.navisa.be.foreigner.service.ForeignerProfileCrudService;
import com.navisa.be.foreigner.service.ForeignerProfileDetailService;
import com.navisa.be.foreigner.service.ForeignerRegistrationService;
import com.navisa.be.global.web.resolver.LoginUserResolver;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.support.ForeignerFixture;
import com.navisa.be.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForeignerProfileController.class)
class ForeignerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForeignerRegistrationService registrationWorkflow;

    @MockitoBean
    private ForeignerProfileDetailService foreignerProfileDetailService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private LoginUserResolver loginUserResolver;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ForeignerProfileCrudService foreignerProfileCrudService;

    private final String mockEmail = "test@navisa.com";

    @BeforeEach
    void setUp() throws Exception {
        Claims mockClaims = Jwts.claims().subject(mockEmail).add("userId", java.util.UUID.randomUUID().toString()).add("userType", "FILLED_FOREIGNER").build();

        given(jwtProvider.getClaims(anyString())).willReturn(mockClaims);

        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);

        given(loginUserResolver.supportsParameter(any())).willReturn(true);
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(mockEmail);
    }

    @Test
    @DisplayName("외국인 상세 요건 상태 조회 성공 시 200 OK를 반환한다.")
    void checkForeignerFilledStatus_Success() throws Exception {
        // given
        UUID profileId = UUID.randomUUID();
        ForeignerStatusResponse response = new ForeignerStatusResponse(profileId, true);
        given(foreignerProfileDetailService.checkForeignerFilledStatus(mockEmail)).willReturn(response);

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
        given(jwtProvider.getClaims(anyString()))
                .willThrow(new AuthException(ResponseStatus.INVALID_TOKEN));

        // when & then
        mockMvc.perform(get("/api/foreigner/requirements")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));
    }

    @Test
    @DisplayName("행정사의 외국인 상세 조회 성공 시 200 OK를 반환한다.")
    void findForeignerDetail_Success() throws Exception {
        // given
        given(authService.checkUserType(anyString(), any())).willReturn(true);
        ForeignerDetailResponse response = ForeignerFixture.createFindForeignerDetailResponse();
        given(foreignerProfileDetailService.findForeignerDetail(any(ForeignerDetailRequest.class))).willReturn(response);

        UUID foreignerId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get("/api/foreigner/" + foreignerId)
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.basicInfo.nickname").value(response.basicInfo().nickname()));
    }

    @Test
    @DisplayName("외국인 진행 상태 조회 성공 시 200 OK를 반환한다.")
    void getForeignerProgress_Success() throws Exception {
        // given
        ForeignerProgressResponse mockResponse = new ForeignerProgressResponse(true, false, true, true, 134L);
        given(authService.checkUserType(anyString(), any())).willReturn(true);
        given(foreignerProfileDetailService.getForeignerProgress(mockEmail)).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/foreigner/progress")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.chatRoomId").value(134))
                .andExpect(jsonPath("$.result.isMatched").value(true));
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 정보를 조회할 경우 400 Bad Request를 반환한다.")
    void checkForeignerFilledStatus_NotFound() throws Exception {
        // given
        given(foreignerProfileDetailService.checkForeignerFilledStatus(mockEmail))
                .willThrow(new ForeignerException(ResponseStatus.INVALID_FOREIGNER));

        // when & then
        mockMvc.perform(get("/api/foreigner/requirements")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ResponseStatus.INVALID_FOREIGNER.getMessage()));
    }

    @Test
    @DisplayName("매칭 내역이 없는 외국인이 진행 상태 조회 시 초기 상태 객체를 반환한다.")
    void getForeignerProgress_EmptyData() throws Exception {
        // given
        given(authService.checkUserType(anyString(), any())).willReturn(true);
        given(foreignerProfileDetailService.getForeignerProgress(mockEmail))
                .willReturn(new ForeignerProgressResponse(false, false, false, false, null));

        // when & then
        mockMvc.perform(get("/api/foreigner/progress")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isMatched").value(false))
                .andExpect(jsonPath("$.result.chatRoomId").doesNotExist());
    }
}
