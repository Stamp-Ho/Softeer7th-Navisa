package com.navisa.be.chat.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class ProposalException extends BaseException {
    public ProposalException(ResponseStatus status) {
        super(status);
    }

    public ProposalException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
