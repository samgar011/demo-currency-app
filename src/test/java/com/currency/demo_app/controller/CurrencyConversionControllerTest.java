package com.currency.demo_app.controller;

import com.currency.demo_app.dto.BulkConversionResultDTO;
import com.currency.demo_app.dto.request.CurrencyConversionFilterRequestDTO;
import com.currency.demo_app.dto.request.CurrencyConversionRequestDTO;
import com.currency.demo_app.dto.response.CurrencyConversionListResponseDTO;
import com.currency.demo_app.dto.response.CurrencyConversionResponseDTO;
import com.currency.demo_app.service.CurrencyConversionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyConversionController.class)
@ActiveProfiles("test")
class CurrencyConversionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CurrencyConversionService conversionService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public CurrencyConversionService mockCurrencyConversionService() {
            return Mockito.mock(CurrencyConversionService.class);
        }
    }

    @Test
    void testConvertCurrency() throws Exception {
        CurrencyConversionRequestDTO request = new CurrencyConversionRequestDTO();
        request.setAmount(BigDecimal.TEN);
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");

        CurrencyConversionResponseDTO response = CurrencyConversionResponseDTO.builder()
                .transactionId("tx123")
                .sourceCurrency("USD")
                .targetCurrency("EUR")
                .sourceAmount(BigDecimal.TEN)
                .convertedAmount(new BigDecimal("9.5"))
                .exchangeRate(new BigDecimal("0.95"))
                .build();

        Mockito.when(conversionService.convertCurrency(any())).thenReturn(response);

        mockMvc.perform(post("/api/conversion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("tx123"));
    }

    @Test
    void testFilterConversions() throws Exception {
        CurrencyConversionFilterRequestDTO request = new CurrencyConversionFilterRequestDTO();
        request.setTransactionId("tx123");
        request.setPage(0);
        request.setSize(10);

        CurrencyConversionListResponseDTO response = CurrencyConversionListResponseDTO.builder()
                .conversions(List.of())
                .totalElements(0L)
                .totalPages(1)
                .currentPage(0)
                .build();

        Mockito.when(conversionService.filterConversions(any())).thenReturn(response);

        mockMvc.perform(post("/api/conversion/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void testBulkUpload() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv",
                "amount,source,target\n100,USD,EUR".getBytes());

        Mockito.when(conversionService.processBulkFile(any(), any(Boolean.class)))
                .thenReturn(List.of(new BulkConversionResultDTO()));

        mockMvc.perform(multipart("/api/conversion/bulk")
                        .file(mockFile)
                        .param("useExternalService", "true"))
                .andExpect(status().isOk());
    }
}