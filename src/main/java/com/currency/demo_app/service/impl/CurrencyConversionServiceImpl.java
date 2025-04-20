package com.currency.demo_app.service.impl;

import com.currency.demo_app.dto.BulkConversionResultDTO;
import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import com.currency.demo_app.dto.request.CurrencyConversionRequestDTO;
import com.currency.demo_app.dto.response.CurrencyConversionListResponseDTO;
import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;
import com.currency.demo_app.enums.ErrorCode;
import com.currency.demo_app.exceptions.*;
import com.currency.demo_app.mapper.CurrencyConversionMapper;
import com.currency.demo_app.model.CurrencyConversion;
import com.currency.demo_app.repository.CurrencyConversionRepository;
import com.currency.demo_app.service.CurrencyConversionService;
import com.currency.demo_app.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.currency.demo_app.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateService exchangeRateService;
    private final CurrencyConversionRepository conversionRepository;
    private final CurrencyConversionMapper conversionMapper;

    @Override
    public CurrencyConversionResponseDTO convertCurrency(CurrencyConversionRequestDTO request) {
        log.info("Starting currency conversion: {}", request);
        try {
            BigDecimal amount = request.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Invalid amount received: {}", amount);
                throw new InvalidRequestException(INVALID_AMOUNT);
            }

            String sourceCurrency = request.getSourceCurrency().toUpperCase();
            String targetCurrency = request.getTargetCurrency().toUpperCase();

            log.debug("Fetching exchange rate from {} to {}", sourceCurrency, targetCurrency);
            BigDecimal exchangeRate = exchangeRateService
                    .getExchangeRate(sourceCurrency, targetCurrency, request.isUseExternalApi())
                    .getRate();

            BigDecimal convertedAmount = amount.multiply(exchangeRate);
            log.debug("Converted amount: {} * {} = {}", amount, exchangeRate, convertedAmount);

            CurrencyConversion saved = conversionRepository.save(
                    CurrencyConversion.builder()
                            .transactionId(UUID.randomUUID().toString())
                            .sourceCurrency(sourceCurrency)
                            .targetCurrency(targetCurrency)
                            .sourceAmount(amount)
                            .convertedAmount(convertedAmount)
                            .exchangeRate(exchangeRate)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            CurrencyConversionResponseDTO response = conversionMapper.convert(saved);
            log.info("Currency conversion completed: {}", response);
            return response;

        } catch (ServiceException e) {
            log.error("ServiceException occurred during conversion: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during currency conversion", e);
            throw new ConversionException(CONVERSION_ERROR, e.getMessage());
        }
    }

    @Cacheable(
            value = "conversionFilters",
            key = "T(com.currency.demo_app.util.CacheKeyUtil).filterKey(#request)"
    )
    @Override
    public CurrencyConversionListResponseDTO filterConversions(CurrencyConversionFilterRequestDTO request) {
        log.info("Filtering currency conversions: {}", request);

        if (request.getTransactionId() == null && request.getDate() == null) {
            log.warn("Invalid filter request. No transactionId or date provided.");
            throw new InvalidRequestException(INVALID_FILTER);
        }

        try {
            PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());
            Page<CurrencyConversion> page;

            if (request.getTransactionId() != null) {
                log.debug("Filtering by transaction ID: {}", request.getTransactionId());
                page = conversionRepository.findByTransactionId(request.getTransactionId(), pageRequest);
            } else {
                LocalDateTime startOfDay = request.getDate().atStartOfDay();
                LocalDateTime endOfDay = request.getDate().atTime(LocalTime.MAX);
                log.debug("Filtering by date range: {} to {}", startOfDay, endOfDay);
                page = conversionRepository.findByCreatedAtBetween(startOfDay, endOfDay, pageRequest);
            }

            List<CurrencyConversionResponseDTO> conversions = page.getContent().stream()
                    .map(conversionMapper::convert)
                    .collect(Collectors.toList());

            CurrencyConversionListResponseDTO response = CurrencyConversionListResponseDTO.builder()
                    .conversions(conversions)
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .currentPage(page.getNumber())
                    .build();

            log.info("Filtering completed. Total records: {}", conversions.size());
            return response;

        } catch (Exception e) {
            log.error("Error while filtering conversions", e);
            throw new FilterException(FILTER_ERROR, e.getMessage());
        }
    }

    @Override
    @Cacheable(
            value = "bulk",
            key = "T(com.currency.demo_app.util.CacheKeyUtil).bulkKey(#file, #useExternal)"
    )
    public List<BulkConversionResultDTO> processBulkFile(MultipartFile file, boolean useExternal) {
        log.info("Processing bulk file. Filename: {}, useExternal: {}", file.getOriginalFilename(), useExternal);

        if (file.isEmpty()) {
            log.warn("Uploaded file is empty.");
            throw new FileProcessingException(EMPTY_FILE);
        }

        if (!"text/csv".equals(file.getContentType())) {
            log.warn("Invalid file type: {}", file.getContentType());
            throw new FileProcessingException(INVALID_FILE_TYPE);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<BulkConversionResultDTO> results = reader.lines()
                    .skip(1)
                    .map(new Function<String, BulkConversionResultDTO>() {
                        int lineNumber = 2;

                        @Override
                        public BulkConversionResultDTO apply(String line) {
                            log.debug("Processing line {}: {}", lineNumber, line);
                            BulkConversionResultDTO result = processBulkLine(line, lineNumber++, useExternal);

                            if (useExternal) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(1000);
                                } catch (InterruptedException e) {
                                    log.error("Thread interrupted during delay", e);
                                    Thread.currentThread().interrupt();
                                    throw new FileProcessingException(PROCESSING_INTERRUPTED);
                                }
                            }

                            return result;
                        }
                    })
                    .collect(Collectors.toList());

            log.info("Bulk file processing complete. Processed {} records.", results.size());
            return results;

        } catch (IOException e) {
            log.error("Error reading file", e);
            throw new FileProcessingException(FILE_READ_ERROR, e.getMessage());
        }
    }

    private BulkConversionResultDTO processBulkLine(String line, int lineNumber, boolean useExternal) {
        try {
            String[] tokens = line.trim().split("\\s*,\\s*");
            if (tokens.length != 3) {
                log.warn("Invalid CSV format at line {}: {}", lineNumber, line);
                throw new FileProcessingException(ErrorCode.INVALID_CSV_FORMAT);
            }

            BigDecimal amount = new BigDecimal(tokens[0].trim());
            String source = tokens[1].trim().toUpperCase();
            String target = tokens[2].trim().toUpperCase();

            log.debug("Parsed line {}: amount={}, source={}, target={}", lineNumber, amount, source, target);

            CurrencyConversionRequestDTO request = new CurrencyConversionRequestDTO();
            request.setAmount(amount);
            request.setSourceCurrency(source);
            request.setTargetCurrency(target);
            request.setUseExternalApi(useExternal);

            CurrencyConversionResponseDTO response = convertCurrency(request);

            return BulkConversionResultDTO.builder()
                    .transactionId(response.getTransactionId())
                    .sourceCurrency(source)
                    .targetCurrency(target)
                    .sourceAmount(amount)
                    .convertedAmount(response.getConvertedAmount())
                    .exchangeRate(response.getExchangeRate())
                    .build();

        } catch (Exception e) {
            log.error("Error processing line {}: {}", lineNumber, e.getMessage());
            return BulkConversionResultDTO.builder()
                    .transactionId("ERROR")
                    .sourceCurrency("N/A")
                    .targetCurrency("N/A")
                    .errorMessage("Line " + lineNumber + ": " + e.getMessage())
                    .sourceAmount(BigDecimal.ZERO)
                    .convertedAmount(BigDecimal.ZERO)
                    .exchangeRate(BigDecimal.ZERO)
                    .build();
        }
    }
}
