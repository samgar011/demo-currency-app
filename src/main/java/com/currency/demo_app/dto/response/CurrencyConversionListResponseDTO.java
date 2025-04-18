package com.currency.demo_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversionListResponseDTO implements Serializable {
    private List<CurrencyConversionResponseDTO> conversions;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}