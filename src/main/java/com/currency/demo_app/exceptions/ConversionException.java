package com.currency.demo_app.exceptions;

import com.currency.demo_app.enums.ErrorCode;

public class ConversionException extends ServiceException {
    public ConversionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConversionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}