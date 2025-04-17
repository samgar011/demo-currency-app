package com.currency.demo_app.service;

import com.currency.demo_app.dto.BulkConversionResultDTO;
import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import com.currency.demo_app.dto.request.CurrencyConversionRequestDTO;
import com.currency.demo_app.dto.response.CurrencyConversionListResponseDTO;
import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CurrencyConversionService {
    CurrencyConversionResponseDTO convertCurrency(CurrencyConversionRequestDTO request);

    CurrencyConversionListResponseDTO filterConversions(CurrencyConversionFilterRequestDTO request);
    List<BulkConversionResultDTO> processBulkFile(MultipartFile file, boolean useExternal);
}