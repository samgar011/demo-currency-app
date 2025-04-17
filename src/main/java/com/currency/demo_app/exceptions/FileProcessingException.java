package com.currency.demo_app.exceptions;

import com.currency.demo_app.enums.ErrorCode;

public class FileProcessingException extends ServiceException {
    public FileProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileProcessingException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}