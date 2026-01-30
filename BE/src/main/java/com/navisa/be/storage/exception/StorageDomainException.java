package com.navisa.be.storage.exception;

import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.model.enums.ResponseStatus;

public class StorageDomainException extends BaseException{

    public StorageDomainException(ResponseStatus responseStatus) {
        super(responseStatus);
    }
}
