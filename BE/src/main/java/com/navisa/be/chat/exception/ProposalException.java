package com.navisa.be.chat.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class ProposalException extends BaseException {
    public ProposalException(ResponseStatus status) {
        super(status);
    }

    public ProposalException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
