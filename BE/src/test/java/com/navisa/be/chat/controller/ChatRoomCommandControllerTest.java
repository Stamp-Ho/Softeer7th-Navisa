package com.navisa.be.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.auth.interceptor.AuthInterceptor;
import com.navisa.be.auth.interceptor.UserTypeCheckInterceptor;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.auth.service.AuthService;
import com.navisa.be.chat.dto.request.CreateChatRoomRequest;
import com.navisa.be.chat.dto.response.CreateChatRoomResponse;
import com.navisa.be.chat.service.ChatRoomCommandService;
import com.navisa.be.chat.service.ChatRoomServiceFacade;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.web.resolver.LoginUserResolver;

import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatRoomCommandController.class)
@Import(UserTypeCheckInterceptor.class)
class ChatRoomCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatRoomCommandService chatRoomCommandService;

    @MockitoBean
    private ChatRoomServiceFacade chatRoomServiceFacade;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @MockitoBean
    private LoginUserResolver loginUserResolver;

    @Test
    @DisplayName("채팅방 생성 요청 시 정상적으로 생성된 채팅방 ID를 반환한다")
    void createChatRoom_Success() throws Exception {
        // given
        UUID opponentProfileId = UUID.randomUUID();
        String content = "안녕하세요";
        ZonedDateTime sendAt = ZonedDateTime.now();
        CreateChatRoomRequest request = new CreateChatRoomRequest(opponentProfileId, content, sendAt);
        String mockEmail = "agent@navisa.com";
        Long createdChatRoomId = 1L;

        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);
        given(loginUserResolver.supportsParameter(any())).willReturn(true);
        given(loginUserResolver.resolveArgument(any(), any(), any(), any())).willReturn(mockEmail);
        given(authService.checkUserType(eq(mockEmail), any())).willReturn(true);
        given(chatRoomCommandService.create(any(CreateChatRoomRequest.class), anyString()))
                .willReturn(new CreateChatRoomResponse(createdChatRoomId));

        // when & then
        mockMvc.perform(post("/api/chatroom")
                .header("Authorization", "Bearer test-token")
                .requestAttr("email", mockEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.chatRoomId").value(createdChatRoomId));
    }

    @Test
    @DisplayName("권한이 없는 사용자가 채팅방 생성을 요청하면 403 Forbidden을 반환한다")
    void createChatRoom_InvalidAgent() throws Exception {
        // given
        UUID opponentProfileId = UUID.randomUUID();
        CreateChatRoomRequest request = new CreateChatRoomRequest(opponentProfileId, "안녕하세요", ZonedDateTime.now());
        String mockEmail = "invalid@navisa.com";

        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);

        given(authService.checkUserType(eq(mockEmail), any())).willReturn(false);

        // when & then
        mockMvc.perform(post("/api/chatroom")
                .header("Authorization", "Bearer test-token")
                .requestAttr("email", mockEmail)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(ResponseStatus.FORBIDDEN.getMessage()));
    }

    @Test
    @DisplayName("채팅방 생성 요청 시에 채팅 내용이 비어 있으면 예외가 발생한다")
    void createChatroom_shouldThrowException_whenEmptyMessage() throws Exception {
        // given
        UUID opponentProfileId = UUID.randomUUID();
        CreateChatRoomRequest request = new CreateChatRoomRequest(opponentProfileId, "", ZonedDateTime.now()); //
        String mockEmail = "invalid@navisa.com";

        given(authInterceptor.preHandle(any(), any(), any())).willReturn(true);
        given(jwtProvider.getEmail(anyString())).willReturn(mockEmail);

        given(authService.checkUserType(eq(mockEmail), any())).willReturn(true);

        // when & then
        mockMvc.perform(post("/api/chatroom")
                        .header("Authorization", "Bearer test-token")
                        .requestAttr("email", mockEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("채팅 내용은 비어 있을 수 없습니다")));
    }
}
