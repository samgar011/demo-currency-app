package com.currency.demo_app.exceptions;

import com.currency.demo_app.enums.ErrorCode;

public class FilterException extends ServiceException {
    public FilterException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FilterException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}