package com.currency.demo_app.dto.request;

import lombok.Data;

@Data
public class CurrencyConversionRequestDTO {
    private String sourceCurrency;
    private String targetCurrency;
    private Double amount;
}