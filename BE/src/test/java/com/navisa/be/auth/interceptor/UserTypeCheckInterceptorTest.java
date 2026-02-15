package com.navisa.be.auth.interceptor;

import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.global.web.annotation.HasUserType;
import com.navisa.be.global.web.annotation.LoginUser;
import com.navisa.be.global.web.response.BaseResponse;
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
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Import(UserTypeCheckInterceptorTest.TestUserTypeCheckController.class)
@AutoConfigureMockMvc
class UserTypeCheckInterceptorTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @RestController
    static class TestUserTypeCheckController {
        @HasUserType({UserType.FILLED_FOREIGNER, UserType.VALID_AGENT})
        @GetMapping("/api/test/usertype/based/auth")
        public BaseResponse<String> getMyInfo(@LoginUser String email) {
            return new BaseResponse<>(email + "님 인가 성공");
        }
    }

    @Test
    @DisplayName("유효한 권한 중 하나로 접근 시 인가에 성공해야 한다")
    void userTypeCheck_succeed_whenOnePermittedUserType() throws Exception {
        // given
        User foreigner = new User("email1", "hash", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true);
        User savedForeigner = userRepository.save(foreigner);
        String accessToken = jwtProvider.createAccessToken(savedForeigner.getEmail());

        // when & then
        mockMvc.perform(get("/api/test/usertype/based/auth")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유효한 권한 중 다른 하나로 접근 시 인가에 성공해야 한다")
    void userTypeCheck_succeed_whenAnotherPermittedUserType() throws Exception {
        // given
        User agent = new User("email1", "hash", UserType.VALID_AGENT, LoginType.EMAIL, true);
        User savedAgent = userRepository.save(agent);
        String accessToken = jwtProvider.createAccessToken(savedAgent.getEmail());

        // when & then
        mockMvc.perform(get("/api/test/usertype/based/auth")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유효하지 않은 권한으로 접속하면 인가에 실패해야 한다")
    void userTypeCheck_fail_whenNotPermittedUserType() throws Exception {
        // given
        User admin = new User("email1", "hash", UserType.ADMIN, LoginType.EMAIL, true);
        User savedAdmin = userRepository.save(admin);
        String accessToken = jwtProvider.createAccessToken(savedAdmin.getEmail());

        // when & then
        mockMvc.perform(get("/api/test/usertype/based/auth")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }
}