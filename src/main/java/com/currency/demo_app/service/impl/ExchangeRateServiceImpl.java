package com.currency.demo_app.service.impl;

import com.currency.demo_app.dto.response.ExchangeRateApiResponse;
import com.currency.demo_app.dto.response.ExchangeRateResponseDTO;
import com.currency.demo_app.exceptions.ExchangeRateException;
import com.currency.demo_app.service.ExchangeRateService;
import com.currency.demo_app.util.ParseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static com.currency.demo_app.enums.ErrorCode.*;

@Service
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
        try {
            if (!useExternalApi) {
                return fetchFromFixer(source, target);
            } else {
                return fetchFromCurrencyLayer(source, target);
            }
        } catch (ExchangeRateException e) {
            throw e;
        } catch (Exception e) {
            throw new ExchangeRateException(EXCHANGE_RATE_ERROR, e.getMessage());
        }
    }

    private ExchangeRateResponseDTO fetchFromCurrencyLayer(String source, String target) {
        try {
            var uri = UriComponentsBuilder.fromHttpUrl(currencyLayerUrl)
                    .queryParam("access_key", currencyLayerKey)
                    .queryParam("source", source)
                    .queryParam("currencies", target)
                    .toUriString();

            var response = restTemplate.getForObject(uri, ExchangeRateApiResponse.class);
            var rateKey = source.toUpperCase() + target.toUpperCase();

            if (response == null) {
                throw new ExchangeRateException(CURRENCY_LAYER_ERROR);
            }

            if (!response.isSuccess()) {
                throw new ExchangeRateException(CURRENCY_LAYER_ERROR,
                        response.getError().getInfo());
            }

            if (response.getQuotes() == null || !response.getQuotes().containsKey(rateKey)) {
                throw new ExchangeRateException(RATE_UNAVAILABLE, rateKey);
            }

            return new ExchangeRateResponseDTO(
                    source.toUpperCase(),
                    target.toUpperCase(),
                    response.getQuotes().get(rateKey)
            );

        } catch (Exception e) {
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

            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

            if (response == null) {
                throw new ExchangeRateException(FIXER_ERROR);
            }

            if (!Boolean.TRUE.equals(response.get("success"))) {
                String errorInfo = Optional.ofNullable(response.get("error"))
                        .map(Object::toString)
                        .orElse(FIXER_UNKNOWN_ERROR.getDefaultMessage());
                throw new ExchangeRateException(FIXER_ERROR, errorInfo);
            }

            Map<String, Object> rates = ParseUtil.safeCastToMap(response.get("rates"));
            if (rates == null || !rates.containsKey(target.toUpperCase())) {
                throw new ExchangeRateException(RATE_UNAVAILABLE, source + " to " + target);
            }

            BigDecimal exchangeRate = ParseUtil.parseRate(rates.get(target.toUpperCase()));

            return new ExchangeRateResponseDTO(
                    source.toUpperCase(),
                    target.toUpperCase(),
                    exchangeRate
            );

        } catch (ClassCastException e) {
            throw new ExchangeRateException(FIXER_ERROR);
        } catch (ExchangeRateException e) {
            throw e;
        } catch (Exception e) {
            throw new ExchangeRateException(FIXER_ERROR, e.getMessage());
        }
    }
}
