package com.navisa.be.chat.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class WebSocketConnectionException extends BaseException {
    public WebSocketConnectionException(ResponseStatus status) {
        super(status);
    }

    public WebSocketConnectionException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
