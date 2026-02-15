package com.navisa.be.global.common.exception;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.web.response.ResponseStatus;

public class StorageException extends BaseException{

    public StorageException(ResponseStatus responseStatus) {
        super(responseStatus);
    }
}
