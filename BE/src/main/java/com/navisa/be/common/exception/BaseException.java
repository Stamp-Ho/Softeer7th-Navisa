package com.navisa.be.common.exception;

import com.navisa.be.common.domain.enums.ResponseStatus;

public class BaseException extends RuntimeException {
    public final ResponseStatus status;

    public BaseException(ResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    // 메시지를 동적으로 변경하고 싶을 때 사용
    public BaseException(ResponseStatus status, String customMessage) {
        super(customMessage);
        this.status = status;
    }
}