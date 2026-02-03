package com.navisa.be.agent.exception;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.exception.BaseException;

public class AgentException extends BaseException {

    public AgentException(ResponseStatus status) {
        super(status);
    }

    public AgentException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
