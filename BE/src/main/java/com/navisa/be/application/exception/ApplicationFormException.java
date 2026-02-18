package com.navisa.be.application.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class ApplicationFormException extends BaseException {

    public ApplicationFormException(ResponseStatus status) {super(status);}

    public ApplicationFormException(ResponseStatus status, String customMessage) {super(status, customMessage); }
}
