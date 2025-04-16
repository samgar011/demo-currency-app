package com.currency.demo_app.service.impl;

import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import com.currency.demo_app.dto.request.CurrencyConversionRequestDTO;
import com.currency.demo_app.dto.response.CurrencyConversionListResponseDTO;
import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;
import com.currency.demo_app.model.CurrencyConversion;
import com.currency.demo_app.repository.CurrencyConversionRepository;
import com.currency.demo_app.service.CurrencyConversionService;
import com.currency.demo_app.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateService exchangeRateService;
    private final CurrencyConversionRepository conversionRepository;

    @Override
    public CurrencyConversionResponseDTO convertCurrency(CurrencyConversionRequestDTO request) {
        Double exchangeRate = exchangeRateService
                .getExchangeRate(request.getSourceCurrency(), request.getTargetCurrency())
                .getRate();

        Double convertedAmount = request.getAmount() * exchangeRate;
        String transactionId = UUID.randomUUID().toString();

        CurrencyConversion saved = conversionRepository.save(
                CurrencyConversion.builder()
                        .transactionId(transactionId)
                        .sourceCurrency(request.getSourceCurrency().toUpperCase())
                        .targetCurrency(request.getTargetCurrency().toUpperCase())
                        .sourceAmount(request.getAmount())
                        .convertedAmount(convertedAmount)
                        .exchangeRate(exchangeRate)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return CurrencyConversionResponseDTO.builder()
                .transactionId(saved.getTransactionId())
                .sourceCurrency(saved.getSourceCurrency())
                .targetCurrency(saved.getTargetCurrency())
                .sourceAmount(saved.getSourceAmount())
                .convertedAmount(saved.getConvertedAmount())
                .exchangeRate(saved.getExchangeRate())
                .build();
    }

    @Override
    public CurrencyConversionListResponseDTO filterConversions(CurrencyConversionFilterRequestDTO request) {
        if (request.getTransactionId() == null && request.getDate() == null) {
            throw new IllegalArgumentException("At least one filter (transactionId or date) must be provided.");
        }

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
        Page<CurrencyConversion> page;

        if (request.getTransactionId() != null) {
            page = conversionRepository.findByTransactionId(request.getTransactionId(), pageRequest);
        } else {
            LocalDateTime startOfDay = request.getDate().atStartOfDay();
            LocalDateTime endOfDay = request.getDate().atTime(LocalTime.MAX);
            page = conversionRepository.findByCreatedAtBetween(startOfDay, endOfDay, pageRequest);
        }

        return CurrencyConversionListResponseDTO.builder()
                .conversions(page.getContent().stream().map(conversion ->
                        CurrencyConversionResponseDTO.builder()
                                .transactionId(conversion.getTransactionId())
                                .sourceCurrency(conversion.getSourceCurrency())
                                .targetCurrency(conversion.getTargetCurrency())
                                .sourceAmount(conversion.getSourceAmount())
                                .convertedAmount(conversion.getConvertedAmount())
                                .exchangeRate(conversion.getExchangeRate())
                                .build()
                ).collect(Collectors.toList()))
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .build();
    }
}