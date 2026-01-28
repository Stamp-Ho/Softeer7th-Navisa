package com.navisa.be.auth.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class AuthException extends BaseException {

    public AuthException(ResponseStatus status) {
        super(status);
    }
}
