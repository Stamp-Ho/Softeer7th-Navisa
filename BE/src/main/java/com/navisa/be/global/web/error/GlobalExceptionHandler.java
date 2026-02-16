package com.navisa.be.global.web.error;

import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.web.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
     * 스프링 Validation 중 발생하는 에러 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        // 발생한 에러들 중 첫 번째 에러만 가져오기
        // 첫번째 에러는 환경에 따라 다름
        FieldError firstError = e.getBindingResult().getFieldErrors().get(0);

        // 필드명과 메시지 조합
        String errorMessage = String.format("%s에서 검증 실패 : %s", firstError.getField(), firstError.getDefaultMessage());

        log.error("Validation Error - Field: {}, Message: {}", firstError.getField(), firstError.getDefaultMessage());

        BaseResponse<Void> response = new BaseResponse<>(ResponseStatus.BAD_REQUEST, errorMessage);
        return new ResponseEntity<>(response, HttpStatus.valueOf(ResponseStatus.BAD_REQUEST.getCode()));
    }

    /**
     * 지원하지 않는 HTTP method 에러 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage());
        BaseResponse<Void> response = new BaseResponse<>(ResponseStatus.HTTP_METHOD_NOT_ALLOWED, ResponseStatus.HTTP_METHOD_NOT_ALLOWED.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ResponseStatus.HTTP_METHOD_NOT_ALLOWED.getCode()));
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
