package com.currency.demo_app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRateRequestDTO {
    private String sourceCurrency;
    private String targetCurrency;
}