package com.currency.demo_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyConversionRequestDTO {
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Source currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Invalid source currency code")
    private String sourceCurrency;

    @NotBlank(message = "Target currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Invalid target currency code")
    private String targetCurrency;
    private boolean useExternalApi = false;
}

