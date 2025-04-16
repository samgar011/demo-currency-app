package com.currency.demo_app.controller;

import com.currency.demo_app.dto.request.ExchangeRateRequestDTO;
import com.currency.demo_app.dto.response.ExchangeRateResponseDTO;
import com.currency.demo_app.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exchange-rate")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping
    public ResponseEntity<ExchangeRateResponseDTO> getExchangeRate(@RequestBody ExchangeRateRequestDTO request) {
        ExchangeRateResponseDTO response = exchangeRateService.getExchangeRate(
                request.getSourceCurrency(),
                request.getTargetCurrency()
        );
        return ResponseEntity.ok(response);
    }
}