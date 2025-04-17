package com.currency.demo_app.exceptions;

import com.currency.demo_app.enums.ErrorCode;

public abstract class ServiceException extends RuntimeException {
    private final String errorCode;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode.getCode();
    }

    public ServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }
}