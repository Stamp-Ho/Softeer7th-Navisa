package com.navisa.be.global.web.error;

import com.navisa.be.global.web.response.ResponseStatus;

public class BadgeException extends BaseException {

    public BadgeException(ResponseStatus status) {
        super(status);
    }
}
