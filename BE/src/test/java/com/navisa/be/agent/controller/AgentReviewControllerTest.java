package com.navisa.be.agent.controller;

import com.navisa.be.agent.dto.response.FeedbackResponse;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.service.AgentBadgeService;
import com.navisa.be.agent.service.AgentReviewService;
import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.global.web.resolver.LoginUserResolver;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentReviewController.class)
class AgentReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgentReviewService agentReviewService;

    @MockitoBean
    private AgentBadgeService agentBadgeService;

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

    @DisplayName("행정사 후기 사례 최신순 조회 시 200 OK와 리스트를 반환한다.")
    @Test
    void getLatestAgentReviews_Success() throws Exception {
        // given
        List<FeedbackResponse> responses = List.of(
                new FeedbackResponse(
                        1L,
                        "테스트 리뷰 1",
                        UUID.fromString("00000000-0000-0000-0000-000000000001"),
                        "김행정",
                        "https://image.com/1"
                )
        );
        given(agentReviewService.getLatestFeedbacks()).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/home/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result[0].feedbackContent")
                        .value("테스트 리뷰 1"));
    }

    @DisplayName("등록된 후기 사례가 없을 때 404 에러 응답을 반환한다.")
    @Test
    void getLatestAgentReviews_NotFound() throws Exception {
        // given
        given(agentReviewService.getLatestFeedbacks())
                .willThrow(new AgentException(ResponseStatus.AGENT_REVIEW_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/home/feedback"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message")
                        .value("등록된 블로그 사례가 없습니다."));
    }
}
