package com.currency.demo_app.exceptions;


import com.currency.demo_app.enums.ErrorCode;

public class ExchangeRateException extends ServiceException {
    public ExchangeRateException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ExchangeRateException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}