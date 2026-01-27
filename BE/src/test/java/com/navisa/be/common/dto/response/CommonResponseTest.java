package com.navisa.be.common.dto.response;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommonResponseTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("성공 응답 시 커스텀 메시지와 결과값이 포함되어야 한다")
    void successResponseTest() throws Exception {
        mockMvc.perform(get("/test/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200)) // isSuccess 제외, code 확인
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.result").value("Hello World"));
    }

    @Test
    @DisplayName("BaseException 발생 시 규격화된 에러 응답이 반환되어야 한다")
    void baseExceptionTest() throws Exception {
        mockMvc.perform(get("/test/base-error"))
                .andExpect(jsonPath("$.code").value(400)) // 에러 코드 확인
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
    }

    @Test
    @DisplayName("정의되지 않은 예외 발생 시 500 에러 포맷이 반환되어야 한다")
    void unhandledExceptionTest() throws Exception {
        mockMvc.perform(get("/test/unhandled-error"))
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("서버 내부 에러가 발생하였습니다."));
    }

    @RestController
    static class TestController {
        @GetMapping("/test/success")
        public BaseResponse<String> success() {
            return new BaseResponse<>("조회 성공", "Hello World");
        }

        @GetMapping("/test/base-error")
        public void baseError() {
            throw new BaseException(ResponseStatus.BAD_REQUEST);
        }

        @GetMapping("/test/unhandled-error")
        public void unhandledError() {
            throw new RuntimeException("예상치 못한 에러");
        }
    }
}
