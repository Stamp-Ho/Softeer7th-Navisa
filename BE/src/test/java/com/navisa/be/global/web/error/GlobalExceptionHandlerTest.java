package com.navisa.be.global.web.error;

import com.navisa.be.global.web.response.ResponseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("지원하지 않는 HTTP Method 요청 시 405 상태코드와 함께 적절한 에러 메시지를 반환한다")
    void handleHttpRequestMethodNotSupportedException() throws Exception {
        // when & then
        mockMvc.perform(post("/test/method-not-supported"))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(ResponseStatus.HTTP_METHOD_NOT_ALLOWED.getCode()))
                .andExpect(jsonPath("$.message").value(ResponseStatus.HTTP_METHOD_NOT_ALLOWED.getMessage()));
    }

    @RestController
    static class TestController {
        @GetMapping("/test/method-not-supported")
        public void methodNotSupported() {
        }
    }
}
