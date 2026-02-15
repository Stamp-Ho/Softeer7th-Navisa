package com.navisa.be.chat.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class ChatRoomException extends BaseException {

    public ChatRoomException(ResponseStatus status) {
        super(status);
    }

    public ChatRoomException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
