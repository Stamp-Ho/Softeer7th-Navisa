package com.navisa.be.chat.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class ChatMessageException extends BaseException {
    public ChatMessageException(ResponseStatus status) {
        super(status);
    }

    public ChatMessageException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
