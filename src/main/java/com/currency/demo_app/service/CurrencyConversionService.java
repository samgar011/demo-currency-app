package com.currency.demo_app.service;

import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import com.currency.demo_app.dto.request.CurrencyConversionRequestDTO;
import com.currency.demo_app.dto.response.CurrencyConversionListResponseDTO;
import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;

public interface CurrencyConversionService {
    CurrencyConversionResponseDTO convertCurrency(CurrencyConversionRequestDTO request);

    CurrencyConversionListResponseDTO filterConversions(CurrencyConversionFilterRequestDTO request);
}