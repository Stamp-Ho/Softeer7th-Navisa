package com.navisa.be.common.dto.response;

import com.navisa.be.common.domain.enums.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({"code", "message", "result"})
public class BaseResponse<T> {

    @Schema(description = "응답 코드", example = "200")
    public final int code;

    @Schema(description = "응답 메시지", example = "요청에 성공하였습니다.")
    public final String message;

    @Schema(description = "응답 결과 데이터")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final T result;

    // 1. 기본 성공 응답
    public BaseResponse(T result) {
        this.code = ResponseStatus.SUCCESS.getCode();
        this.message = ResponseStatus.SUCCESS.getMessage();
        this.result = result;
    }

    // 2. 메시지 직접 지정 성공 응답
    public BaseResponse(String message, T result) {
        this.code = ResponseStatus.SUCCESS.getCode();
        this.message = message;
        this.result = result;
    }

    // 3. 상태 코드와 함께 메시지 직접 지정
    public BaseResponse(ResponseStatus status, String message, T result) {
        this.code = status.getCode();
        this.message = message;
        this.result = result;
    }

    // 4. 결과값 없는 에러 응답용
    public BaseResponse(ResponseStatus status, String message) {
        this.code = status.getCode();
        this.message = message;
        this.result = null;
    }
}