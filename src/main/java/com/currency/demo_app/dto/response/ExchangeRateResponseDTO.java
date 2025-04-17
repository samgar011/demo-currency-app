package com.currency.demo_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExchangeRateResponseDTO {
    private String sourceCurrency;
    private String targetCurrency;
    private BigDecimal rate;
}