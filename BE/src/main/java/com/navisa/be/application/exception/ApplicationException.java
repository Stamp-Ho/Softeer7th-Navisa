package com.navisa.be.application.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class ApplicationException extends BaseException {

    public ApplicationException(ResponseStatus status) {super(status);}

    public ApplicationException(ResponseStatus status, String customMessage) {super(status, customMessage); }
}
