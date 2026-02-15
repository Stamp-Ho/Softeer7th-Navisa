package com.navisa.be.admin.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class AdminDomainException extends BaseException {

    public AdminDomainException(ResponseStatus status) {
        super(status);
    }

    public AdminDomainException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
