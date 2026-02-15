package com.navisa.be.chat.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class WebSocketConnectionException extends BaseException {
    public WebSocketConnectionException(ResponseStatus status) {
        super(status);
    }

    public WebSocketConnectionException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
