package com.navisa.be.admin.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class AdminDomainException extends BaseException {

    public AdminDomainException(ResponseStatus status) {
        super(status);
    }

    public AdminDomainException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
