package com.navisa.be.foreigner.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class ForeignerException extends BaseException{

    public ForeignerException(ResponseStatus status) {
        super(status);
    }

    public ForeignerException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
