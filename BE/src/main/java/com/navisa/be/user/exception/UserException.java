package com.navisa.be.user.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class UserException extends BaseException{

    public UserException(ResponseStatus status) {
        super(status);
    }

    public UserException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
