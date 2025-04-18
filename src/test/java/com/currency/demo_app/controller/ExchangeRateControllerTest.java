package com.currency.demo_app.controller;

import com.currency.demo_app.dto.request.ExchangeRateRequestDTO;
import com.currency.demo_app.dto.response.ExchangeRateResponseDTO;
import com.currency.demo_app.service.ExchangeRateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExchangeRateController.class)
class ExchangeRateControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ExchangeRateService exchangeRateService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        @Primary
        public ExchangeRateService mockExchangeRateService() {
            return Mockito.mock(ExchangeRateService.class);
        }
    }

    @Test
    void testGetExchangeRate() throws Exception {
        ExchangeRateRequestDTO request = new ExchangeRateRequestDTO();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setUseExternalApi(false);

        ExchangeRateResponseDTO response = new ExchangeRateResponseDTO("USD", "EUR", new BigDecimal("0.95"));

        Mockito.when(exchangeRateService.getExchangeRate(any(), any(), any(Boolean.class))).thenReturn(response);

        mockMvc.perform(post("/api/exchange-rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rate").value("0.95"));
    }
}