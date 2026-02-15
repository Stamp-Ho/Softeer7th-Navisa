package com.navisa.be.global.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({"code", "message", "result"})
public record BaseResponse<T>(
        @Schema(description = "응답 코드")
        int code,

        @Schema(description = "응답 메시지")
        String message,

        @Schema(description = "응답 결과 데이터")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T result
) {
    // 기본 성공 응답
    public BaseResponse(T result) {
        this(ResponseStatus.SUCCESS.getCode(), ResponseStatus.SUCCESS.getMessage(), result);
    }

    // 메시지 직접 지정 성공 응답
    public BaseResponse(String message, T result) {
        this(ResponseStatus.SUCCESS.getCode(), message, result);
    }

    // 결과값 없는 에러 응답용
    public BaseResponse(ResponseStatus status, String message) {
        this(status.getCode(), message, null);
    }
}
