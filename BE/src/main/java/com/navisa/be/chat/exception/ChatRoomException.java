package com.navisa.be.chat.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class ChatRoomException extends BaseException {

    public ChatRoomException(ResponseStatus status) {
        super(status);
    }

    public ChatRoomException(ResponseStatus status, String customMessage) {
        super(status, customMessage);
    }
}
