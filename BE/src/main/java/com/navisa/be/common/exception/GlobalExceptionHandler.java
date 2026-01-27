package com.navisa.be.common.exception;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 중 발생하는 커스텀 예외 처리
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException e) {

        log.error("BaseException: {}", e.getMessage());
        BaseResponse<Void> response = new BaseResponse<>(e.status, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.status.getCode()));
    }

    /**
     * 그 외 예상치 못한 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleException(Exception e) {

        log.error("Unhandled Exception: ", e);
        BaseResponse<Void> response = new BaseResponse<>(ResponseStatus.SERVER_ERROR, "서버 내부 에러가 발생하였습니다.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
