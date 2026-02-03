package com.navisa.be.user.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class UserException extends BaseException{

    public UserException(ResponseStatus status) {
        super(status);
    }

    public UserException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
