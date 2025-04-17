package com.currency.demo_app.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateApiResponse {
    private boolean success;
    private String source;
    private Map<String, BigDecimal> quotes;
    private ErrorResponse error;
    public boolean isSuccess() {
        return success;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class ErrorResponse {
        private int code;
        private String info;
    }


}