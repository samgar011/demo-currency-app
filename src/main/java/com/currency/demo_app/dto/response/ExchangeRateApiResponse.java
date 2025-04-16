package com.currency.demo_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateApiResponse {
    private boolean success;
    private String source;
    private Map<String, Double> quotes;

    public boolean isSuccess() {
        return success;
    }
}