package com.currency.demo_app.mapper;

import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;
import com.currency.demo_app.model.CurrencyConversion;
import org.springframework.stereotype.Component;

@Component
public class CurrencyConversionMapper {
    
    public CurrencyConversionResponseDTO convert(CurrencyConversion conversion) {
        return CurrencyConversionResponseDTO.builder()
                .transactionId(conversion.getTransactionId())
                .sourceCurrency(conversion.getSourceCurrency())
                .targetCurrency(conversion.getTargetCurrency())
                .sourceAmount(conversion.getSourceAmount())
                .convertedAmount(conversion.getConvertedAmount())
                .exchangeRate(conversion.getExchangeRate())
                .build();
    }
}