package com.navisa.be.chat.controller;

import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.chat.dto.response.ChatMessageCountResponse;
import com.navisa.be.chat.dto.response.ChatRoomCardResponse;
import com.navisa.be.chat.dto.response.ChatRoomParticipantsInfoResponse;
import com.navisa.be.chat.service.ChatMessageFlowService;
import com.navisa.be.chat.service.ChatRoomSearchService;
import com.navisa.be.chat.service.ChatRoomInteractService;
import com.navisa.be.global.web.request.SliceRequest;
import com.navisa.be.global.web.resolver.LoginUserResolver;
import com.navisa.be.global.web.resolver.SliceInfoArgumentResolver;
import com.navisa.be.global.web.response.SliceResponse;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatRoomSearchController.class)
@Import({UserTypeCheckInterceptor.class, SliceInfoArgumentResolver.class, LoginUserResolver.class})
class ChatRoomSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatRoomInteractService chatRoomInteractService;

    @MockitoBean
    private ChatMessageFlowService chatMessageFlowService;

    @MockitoBean
    private ChatRoomSearchService chatRoomSearchService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @Test
    @DisplayName("채팅방 목록 조회 시 정상적으로 응답한다")
    void getChatRooms_Success() throws Exception {
        // given
        String mockEmail = "test@navisa.com";
        SliceResponse<ChatRoomCardResponse, Long> response = new SliceResponse<>(Collections.emptyList(), false, null);

        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);
        given(authService.checkUserType(anyString(), any())).willReturn(true);
        given(chatRoomInteractService.findAllChatRoomsByNoOffset(eq(mockEmail), any(), any(SliceRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/chatrooms")
                        .header("Authorization", "Bearer test-token")
                        .requestAttr("email", mockEmail)
                        .requestAttr("userType", "VALID_AGENT")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("안 읽은 메시지 수 조회 시 정상적으로 응답한다")
    void getChatMessageNonReadCount_Success() throws Exception {
        // given
        String mockEmail = "test@navisa.com";
        ChatMessageCountResponse response = new ChatMessageCountResponse(5L);

        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);
        given(authService.checkUserType(anyString(), any())).willReturn(true);
        given(chatMessageFlowService.findNonReadCountByUserEmail(mockEmail))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/chatrooms/nonread/count")
                        .header("Authorization", "Bearer test-token")
                        .requestAttr("email", mockEmail)
                        .requestAttr("userType", "VALID_AGENT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.count").value(5));
    }

    @Test
    @DisplayName("행정사의 매칭된 채팅방 중 안 읽은 메시지 수 조회 시 정상적으로 응답한다")
    void getChatMessageMatchedNonReadCount_Success() throws Exception {
        // given
        String mockEmail = "agent@navisa.com";
        ChatMessageCountResponse response = new ChatMessageCountResponse(3L);

        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);
        given(authService.checkUserType(anyString(), any())).willReturn(true);
        given(chatMessageFlowService.findMatchedNonReadCountByUserEmail(mockEmail))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/chatrooms/matched/count")
                        .header("Authorization", "Bearer test-token")
                        .requestAttr("email", mockEmail)
                        .requestAttr("userType", "VALID_AGENT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.count").value(3));
    }

    @Test
    @DisplayName("특정 채팅방 참여자 정보 조회 시 정상적으로 응답한다")
    void getChatRoomParticipantsInfo_Success() throws Exception {
        // given
        String mockEmail = "test@navisa.com";
        Long chatRoomId = 1L;
        UUID agentId = UUID.randomUUID();
        ChatRoomParticipantsInfoResponse response = new ChatRoomParticipantsInfoResponse(
                new ChatRoomParticipantsInfoResponse.AgentInfo(agentId, List.of(1L, 2L), "행정사", null),
                new ChatRoomParticipantsInfoResponse.ForeignerInfo(UUID.randomUUID(), "외국인", "특정직무", LocalDate.now(), List.of(1L, 2L), false),
                false
        );

        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);
        given(authService.checkUserType(anyString(), any())).willReturn(true);
        given(chatRoomSearchService.findParticipantsInfoById(chatRoomId, mockEmail))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/chatrooms/{roomId}/participants-info", chatRoomId)
                        .header("Authorization", "Bearer test-token")
                        .requestAttr("email", mockEmail)
                        .requestAttr("userType", "VALID_AGENT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.agentInfo.agentId").value(agentId.toString()));
    }
}
