package com.navisa.be.agent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.agent.dto.*;
import com.navisa.be.agent.dto.request.RegisterAgentProfileRequest;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.Badge;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class AgentProfileIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Test
    @DisplayName("에이전트 프로필 등록 성공에 성공한다")
    void registerAgentProfile_shouldReturnOk() throws Exception {
        // given
        User user = userTestFixture.createUser("email", UserType.UNVALID_AGENT);
        String accessToken = jwtProvider.createAccessToken(user.getEmail());

        JobCode jobCode = agentProfileTestFixture.createJobCode("코드1", "직무1");
        Language lang = agentProfileTestFixture.createLanguage("언어");

        RegisterAgentProfileRequest request = AgentFixture.createRegisterAgentProfileRequest(List.of(jobCode.getId()), List.of(lang.getId()), new LicenseInfoDto("자격증 번호",
                LocalDate.now(),
                "Page-10",
                null));

        // when & then
        mockMvc.perform(post("/api/agent/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print()); // 요청 응답 로그 출력
    }

    @Test
    @DisplayName("에이전트 프로필 등록 요청의 필드가 null이면 400에러를 반환한다")
    void registerAgentProfile_shouldReturnBadRequest_whenRequestConsistOfNull() throws Exception {
        // given
        RegisterAgentProfileRequest request = AgentFixture.getRegisterAgentProfileRequestConsistingOfNull();

        String accessToken = jwtProvider.createAccessToken("email");

        // when & then
        mockMvc.perform(post("/api/agent/profile")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("검증 실패")))
                .andDo(print()); // 요청 응답 로그 출력
    }

    @Test
    @DisplayName("직무코드 조회에 성공한다")
    void getJobCode_shouldReturnJobCodeList() throws Exception {
        // given
        jobCodeRepository.save(new JobCode(null, "C001", "백엔드 개발자", new float[512], null));
        jobCodeRepository.save(new JobCode(null, "C002", "프론트엔드 개발자", new float[512], null));

        String accessToken = jwtProvider.createAccessToken("email");

        // when & then
        mockMvc.perform(get("/api/agent/register-form/jobcodes")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.jobCodeList").isArray())
                .andExpect(jsonPath("$.result.jobCodeList[0].name").value("백엔드 개발자"))
                .andDo(print());
    }

    @Test
    @DisplayName("행정사 상세 조회에 성공한다")
    void getAgentDetail_shouldSucceed() throws Exception {
        // given
        JobCode jobCode = agentProfileTestFixture.createJobCode("직무코드", "직무명");
        Language lang = agentProfileTestFixture.createLanguage("언어");

        User agentUser = userTestFixture.createUser("agent1@test.com", UserType.VALID_AGENT);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "주소", agentUser.getId());

        agentProfileTestFixture.createAgentSpecializedJob(agentProfile, jobCode);
        agentProfileTestFixture.createAgentLanguage(agentProfile, lang);

        Badge badge = agentProfileTestFixture.createBadge(BadgeName.FAST_INFORMATION);
        agentProfileTestFixture.createAgentReview(agentProfile, badge);

        User loginUser = userTestFixture.createUser("agent2@test.com", UserType.FILLED_FOREIGNER);
        foreignerProfileTestFixture.createForeignerProfile(loginUser);

        String accessToken = jwtProvider.createAccessToken(loginUser.getEmail());

        // when & then
        mockMvc.perform(get("/api/agent/" + agentProfile.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result.header.comment").value("Comment"));
    }

}