package com.navisa.be.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.admin.dto.request.AgentPermitRequest;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class AdminControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("ADMIN 타입의 사용자면 행정사 승인에 성공한다")
    void permitNewAgent_shouldSucceed() throws Exception {
        // given
        User admin = new User("email1", "hash", UserType.ADMIN, LoginType.EMAIL, true);
        User savedAdmin = userRepository.save(admin);
        String accessToken = jwtProvider.createAccessToken(savedAdmin.getEmail());

        User newAgent = new User("email2", null, UserType.INVALID_AGENT, LoginType.GOOGLE, true);
        User savedAgent = userRepository.save(newAgent);

        AgentPermitRequest request = new AgentPermitRequest(savedAgent.getId());

        // when & then
        mockMvc.perform(post("/api/admin/permit/agent")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        //
        User user = userRepository.findById(savedAgent.getId()).orElseThrow();
        assertThat(user.getUserType()).isEqualTo(UserType.VALID_AGENT);
    }

    @DisplayName("ADMIN 타입의 사용자가 아니면 행정사 승인에 실패한다")
    @Test
    void permitNewAgent_shouldFail_whenNonAdminUser() throws Exception {
        // given
        User NON_ADMIN_USER = new User("email1", "hash", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true);
        User SAVED_NON_ADMIN = userRepository.save(NON_ADMIN_USER);
        String accessToken = jwtProvider.createAccessToken(SAVED_NON_ADMIN.getEmail());

        User newAgent = new User("email2", null, UserType.INVALID_AGENT, LoginType.GOOGLE, true);
        User savedAgent = userRepository.save(newAgent);

        AgentPermitRequest request = new AgentPermitRequest(savedAgent.getId());

        // when & then
        mockMvc.perform(post("/api/admin/permit/agent")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(ResponseStatus.FORBIDDEN.getMessage()));
    }
}