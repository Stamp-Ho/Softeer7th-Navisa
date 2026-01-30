package com.navisa.be.agent.exception;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.exception.BaseException;

public class AgentProfileDomainException extends BaseException {

    public AgentProfileDomainException(ResponseStatus status) {
        super(status);
    }

    public AgentProfileDomainException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
