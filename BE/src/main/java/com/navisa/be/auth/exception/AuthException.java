package com.navisa.be.auth.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class AuthException extends BaseException {

    public AuthException(ResponseStatus status) {
        super(status);
    }
}
