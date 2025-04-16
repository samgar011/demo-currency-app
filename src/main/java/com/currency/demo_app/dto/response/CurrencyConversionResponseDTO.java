package com.currency.demo_app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyConversionResponseDTO {
    private String transactionId;
    private String sourceCurrency;
    private String targetCurrency;
    private Double sourceAmount;
    private Double convertedAmount;
    private Double exchangeRate;
}