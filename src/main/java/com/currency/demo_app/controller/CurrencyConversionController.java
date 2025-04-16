package com.currency.demo_app.controller;

import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import com.currency.demo_app.dto.request.CurrencyConversionRequestDTO;
import com.currency.demo_app.dto.response.CurrencyConversionListResponseDTO;
import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;
import com.currency.demo_app.service.CurrencyConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversion")
@RequiredArgsConstructor
public class CurrencyConversionController {

    private final CurrencyConversionService conversionService;

    @PostMapping
    public ResponseEntity<CurrencyConversionResponseDTO> convert(@RequestBody CurrencyConversionRequestDTO request) {
        return ResponseEntity.ok(conversionService.convertCurrency(request));
    }

    @PostMapping("/filter")
    public ResponseEntity<CurrencyConversionListResponseDTO> filterConversions(
            @RequestBody CurrencyConversionFilterRequestDTO request) {
        return ResponseEntity.ok(conversionService.filterConversions(request));
    }
}