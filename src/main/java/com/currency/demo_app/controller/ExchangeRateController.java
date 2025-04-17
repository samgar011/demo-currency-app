package com.currency.demo_app.controller;

import com.currency.demo_app.dto.request.ExchangeRateRequestDTO;
import com.currency.demo_app.dto.response.ExchangeRateResponseDTO;
import com.currency.demo_app.service.ExchangeRateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchange-rate")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @PostMapping
    public ResponseEntity<ExchangeRateResponseDTO>
    getExchangeRate(@Valid @RequestBody ExchangeRateRequestDTO request) {
        ExchangeRateResponseDTO rate = exchangeRateService.getExchangeRate(
                request.getSourceCurrency(),
                request.getTargetCurrency(),
                request.isUseExternalApi()
        );
        return ResponseEntity.ok(rate);
    }

}