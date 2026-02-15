package com.navisa.be.agent.exception;

import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.web.error.BaseException;

public class AgentException extends BaseException {

    public AgentException(ResponseStatus status) {
        super(status);
    }

    public AgentException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
