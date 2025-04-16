package com.currency.demo_app.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRateRequestDTO {
    private String sourceCurrency;
    private String targetCurrency;
}