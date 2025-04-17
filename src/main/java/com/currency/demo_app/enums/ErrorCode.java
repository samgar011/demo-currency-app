package com.currency.demo_app.enums;

public enum ErrorCode {
    CONVERSION_ERROR("CONVERSION_ERROR", "Currency conversion failed"),
    FILTER_ERROR("FILTER_ERROR", "Error filtering conversions"),
    INVALID_FILTER("INVALID_FILTER", "Invalid filter request"),
    EMPTY_FILE("EMPTY_FILE", "Uploaded file is empty"),
    INVALID_FILE_TYPE("INVALID_FILE_TYPE", "Only CSV files are allowed"),
    FILE_READ_ERROR("FILE_READ_ERROR", "Failed to read CSV file"),
    PROCESSING_INTERRUPTED("PROCESSING_INTERRUPTED", "Bulk processing was interrupted"),
    CURRENCY_LAYER_ERROR("CURRENCY_LAYER_ERROR", "CurrencyLayer API error"),
    FIXER_ERROR("FIXER_ERROR", "Fixer API error"),
    RATE_UNAVAILABLE("RATE_UNAVAILABLE", "Exchange rate not available"),
    INVALID_RATE_FORMAT("INVALID_RATE_FORMAT", "Invalid rate format from API"),
    EXCHANGE_RATE_ERROR("EXCHANGE_RATE_ERROR", "Failed to get exchange rate"),
    INVALID_AMOUNT("INVALID_AMOUNT", "Amount must be greater than zero.");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}