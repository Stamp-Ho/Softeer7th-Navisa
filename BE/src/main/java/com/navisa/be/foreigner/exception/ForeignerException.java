package com.navisa.be.foreigner.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class ForeignerException extends BaseException{

    public ForeignerException(ResponseStatus status) {
        super(status);
    }

    public ForeignerException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
