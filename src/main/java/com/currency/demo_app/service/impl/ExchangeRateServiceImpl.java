package com.currency.demo_app.service.impl;

import com.currency.demo_app.dto.response.ExchangeRateApiResponse;
import com.currency.demo_app.dto.response.ExchangeRateResponseDTO;
import com.currency.demo_app.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Value("${currencylayer.api.key}")
    private String apiKey;

    @Value("${currencylayer.api.url}")
    private String apiUrl;

    @Override
    public ExchangeRateResponseDTO getExchangeRate(String sourceCurrency, String targetCurrency) {
        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("access_key", apiKey)
                .queryParam("source", sourceCurrency.toUpperCase())
                .queryParam("currencies", targetCurrency.toUpperCase());

        ExchangeRateApiResponse response = restTemplate.getForObject(uriBuilder.toUriString(), ExchangeRateApiResponse.class);

        if (response == null || !response.isSuccess() || response.getQuotes() == null) {
            throw new RuntimeException("Failed to fetch exchange rate from CurrencyLayer.");
        }

        String key = sourceCurrency.toUpperCase() + targetCurrency.toUpperCase();
        Double rate = response.getQuotes().get(key);

        if (rate == null) {
            throw new RuntimeException("Exchange rate not available for: " + key);
        }

        return new ExchangeRateResponseDTO(sourceCurrency.toUpperCase(), targetCurrency.toUpperCase(), rate);
    }
}