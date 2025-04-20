package com.currency.demo_app.service.impl;

import com.currency.demo_app.dto.response.ExchangeRateApiResponse;
import com.currency.demo_app.dto.response.ExchangeRateResponseDTO;
import com.currency.demo_app.exceptions.ExchangeRateException;
import com.currency.demo_app.service.ExchangeRateService;
import com.currency.demo_app.util.ParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static com.currency.demo_app.enums.ErrorCode.*;

@Service
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

    @Value("${currencylayer.api.key}")
    private String currencyLayerKey;

    @Value("${currencylayer.api.url}")
    private String currencyLayerUrl;

    @Value("${fixer.api.key}")
    private String fixerKey;

    @Value("${fixer.api.url}")
    private String fixerUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ExchangeRateResponseDTO getExchangeRate(String source, String target, boolean useExternalApi) {
        log.info("Fetching exchange rate from {} to {}. useExternalApi={}", source, target, useExternalApi);

        try {
            ExchangeRateResponseDTO response = useExternalApi
                    ? fetchFromCurrencyLayer(source, target)
                    : fetchFromFixer(source, target);

            log.info("Exchange rate fetched successfully: {}", response);
            return response;
        } catch (ExchangeRateException e) {
            log.error("Known exchange rate error occurred: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during exchange rate fetch: {}", e.getMessage(), e);
            throw new ExchangeRateException(EXCHANGE_RATE_ERROR, e.getMessage());
        }
    }

    private ExchangeRateResponseDTO fetchFromCurrencyLayer(String source, String target) {
        try {
            String uri = UriComponentsBuilder.fromHttpUrl(currencyLayerUrl)
                    .queryParam("access_key", currencyLayerKey)
                    .queryParam("source", source)
                    .queryParam("currencies", target)
                    .toUriString();

            log.debug("Calling CurrencyLayer API with URI: {}", uri);
            ExchangeRateApiResponse response = restTemplate.getForObject(uri, ExchangeRateApiResponse.class);
            String rateKey = source.toUpperCase() + target.toUpperCase();

            if (response == null) {
                log.error("CurrencyLayer response is null.");
                throw new ExchangeRateException(CURRENCY_LAYER_ERROR);
            }

            if (!response.isSuccess()) {
                log.error("CurrencyLayer returned error: {}", response.getError().getInfo());
                throw new ExchangeRateException(CURRENCY_LAYER_ERROR, response.getError().getInfo());
            }

            if (response.getQuotes() == null || !response.getQuotes().containsKey(rateKey)) {
                log.error("CurrencyLayer quote for {} not found", rateKey);
                throw new ExchangeRateException(RATE_UNAVAILABLE, rateKey);
            }

            BigDecimal rate = response.getQuotes().get(rateKey);
            return new ExchangeRateResponseDTO(source.toUpperCase(), target.toUpperCase(), rate);

        } catch (Exception e) {
            log.error("Error fetching data from CurrencyLayer: {}", e.getMessage(), e);
            throw new ExchangeRateException(CURRENCY_LAYER_ERROR, e.getMessage());
        }
    }

    private ExchangeRateResponseDTO fetchFromFixer(String source, String target) {
        try {
            String uri = UriComponentsBuilder.fromHttpUrl(fixerUrl)
                    .queryParam("access_key", fixerKey)
                    .queryParam("base", source.toUpperCase())
                    .queryParam("symbols", target.toUpperCase())
                    .toUriString();

            log.debug("Calling Fixer API with URI: {}", uri);
            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

            if (response == null) {
                log.error("Fixer API response is null.");
                throw new ExchangeRateException(FIXER_ERROR);
            }

            if (!Boolean.TRUE.equals(response.get("success"))) {
                String errorInfo = Optional.ofNullable(response.get("error"))
                        .map(Object::toString)
                        .orElse(FIXER_UNKNOWN_ERROR.getDefaultMessage());
                log.error("Fixer API returned error: {}", errorInfo);
                throw new ExchangeRateException(FIXER_ERROR, errorInfo);
            }

            Map<String, Object> rates = ParseUtil.safeCastToMap(response.get("rates"));
            if (rates == null || !rates.containsKey(target.toUpperCase())) {
                log.error("Fixer rate not found for {} to {}", source, target);
                throw new ExchangeRateException(RATE_UNAVAILABLE, source + " to " + target);
            }

            BigDecimal exchangeRate = ParseUtil.parseRate(rates.get(target.toUpperCase()));
            return new ExchangeRateResponseDTO(source.toUpperCase(), target.toUpperCase(), exchangeRate);

        } catch (ClassCastException e) {
            log.error("Type casting issue in Fixer response.", e);
            throw new ExchangeRateException(FIXER_ERROR);
        } catch (ExchangeRateException e) {
            log.warn("ExchangeRateException encountered: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching data from Fixer: {}", e.getMessage(), e);
            throw new ExchangeRateException(FIXER_ERROR, e.getMessage());
        }
    }
}
