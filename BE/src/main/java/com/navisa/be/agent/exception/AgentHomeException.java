package com.navisa.be.agent.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class AgentHomeException extends BaseException {

    public AgentHomeException(ResponseStatus status) {
        super(status);
    }

    public AgentHomeException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
