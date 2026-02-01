package com.navisa.be.common.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class BadgeException extends BaseException {

    public BadgeException(ResponseStatus status) {
        super(status);
    }
}
