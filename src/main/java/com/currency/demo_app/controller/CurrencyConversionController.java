package com.currency.demo_app.controller;

import com.currency.demo_app.dto.BulkConversionResultDTO;
import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import com.currency.demo_app.dto.request.CurrencyConversionRequestDTO;
import com.currency.demo_app.dto.response.CurrencyConversionListResponseDTO;
import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;
import com.currency.demo_app.service.CurrencyConversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/conversion")
@RequiredArgsConstructor
@Slf4j
public class CurrencyConversionController {

    private final CurrencyConversionService conversionService;

    @PostMapping
    public ResponseEntity<CurrencyConversionResponseDTO>
    convert(@Valid @RequestBody CurrencyConversionRequestDTO request) {
        log.info("Received currency conversion request: {}", request);
        CurrencyConversionResponseDTO response = conversionService.convertCurrency(request);
        log.info("Conversion completed: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filter")
    public ResponseEntity<CurrencyConversionListResponseDTO> filterConversions(
            @RequestBody CurrencyConversionFilterRequestDTO request) {
        log.info("Filtering conversions with request: {}", request);
        CurrencyConversionListResponseDTO response = conversionService.filterConversions(request);
        log.info("Filtered result: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<BulkConversionResultDTO>> uploadBulkFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean useExternalService) {
        log.info("Received bulk file upload. Filename: {}, useExternalService: {}", file.getOriginalFilename(), useExternalService);
        List<BulkConversionResultDTO> result = conversionService.processBulkFile(file, useExternalService);
        log.info("Bulk conversion processed. Total results: {}", result.size());
        return ResponseEntity.ok(result);
    }
}