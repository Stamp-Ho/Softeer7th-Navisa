package com.navisa.be.chat.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class ChatMessageException extends BaseException {
    public ChatMessageException(ResponseStatus status) {
        super(status);
    }

    public ChatMessageException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
