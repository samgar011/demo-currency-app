package com.currency.demo_app.service;

import com.currency.demo_app.dto.response.ExchangeRateResponseDTO;

public interface ExchangeRateService {
    ExchangeRateResponseDTO getExchangeRate(String sourceCurrency, String targetCurrency, boolean useExternalApi);
}