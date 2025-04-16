package com.currency.demo_app.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRateResponseDTO {
    private String sourceCurrency;
    private String targetCurrency;
    private double rate;
}