package com.currency.demo_app.controller;

import com.currency.demo_app.dto.BulkConversionResultDTO;
import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import com.currency.demo_app.dto.request.CurrencyConversionRequestDTO;
import com.currency.demo_app.dto.response.CurrencyConversionListResponseDTO;
import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;
import com.currency.demo_app.service.CurrencyConversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/conversion")
@RequiredArgsConstructor
public class CurrencyConversionController {

    private final CurrencyConversionService conversionService;

    @PostMapping
    public ResponseEntity<CurrencyConversionResponseDTO>
    convert(@Valid @RequestBody CurrencyConversionRequestDTO request) {
        return ResponseEntity.ok(conversionService.convertCurrency(request));
    }

    @PostMapping("/filter")
    public ResponseEntity<CurrencyConversionListResponseDTO> filterConversions(
            @RequestBody CurrencyConversionFilterRequestDTO request) {
        return ResponseEntity.ok(conversionService.filterConversions(request));
    }

    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<BulkConversionResultDTO>> uploadBulkFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean useExternalService) {
        List<BulkConversionResultDTO> result = conversionService.processBulkFile
                (file, useExternalService);
        return ResponseEntity.ok(result);
    }
}