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
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateService exchangeRateService;
    private final CurrencyConversionRepository conversionRepository;
    private final CurrencyConversionMapper conversionMapper;


    @Override
    public CurrencyConversionResponseDTO convertCurrency(CurrencyConversionRequestDTO
                                                                     request) {
        try {
            BigDecimal amount = request.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidRequestException(INVALID_AMOUNT);
            }

            String sourceCurrency = request.getSourceCurrency().toUpperCase();
            String targetCurrency = request.getTargetCurrency().toUpperCase();

            BigDecimal exchangeRate = exchangeRateService
                    .getExchangeRate(sourceCurrency, targetCurrency,
                            request.isUseExternalApi())
                    .getRate();

            BigDecimal convertedAmount = amount.multiply(exchangeRate);

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

            return conversionMapper.convert(saved);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ConversionException(CONVERSION_ERROR, e.getMessage());
        }
    }

    @Cacheable(
            value = "conversionFilters",
            key = "T(com.currency.demo_app.util.CacheKeyUtil).filterKey(#request)"
    )
    @Override
    public CurrencyConversionListResponseDTO filterConversions
            (CurrencyConversionFilterRequestDTO request) {
        if (request.getTransactionId() == null && request.getDate() == null) {
            throw new InvalidRequestException(INVALID_FILTER);
        }

        try {
            PageRequest pageRequest = PageRequest.of(request.getPage(),
                    request.getSize());
            Page<CurrencyConversion> page;

            if (request.getTransactionId() != null) {
                page = conversionRepository.findByTransactionId(request
                        .getTransactionId(), pageRequest);
            } else {
                LocalDateTime startOfDay = request.getDate().atStartOfDay();
                LocalDateTime endOfDay = request.getDate().atTime(LocalTime.MAX);
                page = conversionRepository.findByCreatedAtBetween(startOfDay,
                        endOfDay, pageRequest);
            }

            List<CurrencyConversionResponseDTO> conversions = page
                    .getContent()
                    .stream()
                    .map(conversionMapper::convert)
                    .collect(Collectors.toList());

            return CurrencyConversionListResponseDTO.builder()
                    .conversions(conversions)
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .currentPage(page.getNumber())
                    .build();

        } catch (Exception e) {
            throw new FilterException(FILTER_ERROR, e.getMessage());
        }
    }

    @Override
    @Cacheable(
            value = "bulk",
            key = "T(com.currency.demo_app.util.CacheKeyUtil).bulkKey(#file, #useExternal)"
    )
    public List<BulkConversionResultDTO> processBulkFile(MultipartFile file,
                                                         boolean useExternal) {
        if (file.isEmpty()) {
            throw new FileProcessingException(EMPTY_FILE);
        }

        if (!"text/csv".equals(file.getContentType())) {
            throw new FileProcessingException(INVALID_FILE_TYPE);
        }

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            return reader.lines()
                    .skip(1)
                    .map(new Function<String, BulkConversionResultDTO>() {
                        int lineNumber = 2;

                        @Override
                        public BulkConversionResultDTO apply(String line) {
                            BulkConversionResultDTO result = processBulkLine(line,
                                    lineNumber++, useExternal);

                            if (useExternal) {
                                try {
                                    TimeUnit.MILLISECONDS.sleep(1000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    throw new FileProcessingException(PROCESSING_INTERRUPTED);
                                }
                            }

                            return result;
                        }
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new FileProcessingException(FILE_READ_ERROR, e.getMessage());
        }
    }

    private BulkConversionResultDTO processBulkLine(String line, int lineNumber,
                                                    boolean useExternal) {
        try {
            String[] tokens = line.trim().split("\\s*,\\s*");
            if (tokens.length != 3) {
                throw new FileProcessingException(ErrorCode.INVALID_CSV_FORMAT);
            }
            BigDecimal amount = new BigDecimal(tokens[0].trim());
            String source = tokens[1].trim().toUpperCase();
            String target = tokens[2].trim().toUpperCase();

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
