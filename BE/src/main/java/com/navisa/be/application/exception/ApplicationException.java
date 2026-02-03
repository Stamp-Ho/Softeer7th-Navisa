package com.navisa.be.application.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class ApplicationException extends BaseException {

    public ApplicationException(ResponseStatus status) {super(status);}

    public ApplicationException(ResponseStatus status, String customMessage) {super(status, customMessage); }
}
