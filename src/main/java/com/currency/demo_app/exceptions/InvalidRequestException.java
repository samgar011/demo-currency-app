package com.currency.demo_app.exceptions;

import com.currency.demo_app.enums.ErrorCode;

public class InvalidRequestException extends ServiceException {
    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode);
    }


}