package com.currency.demo_app.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CurrencyConversionListResponseDTO {
    private List<CurrencyConversionResponseDTO> conversions;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}