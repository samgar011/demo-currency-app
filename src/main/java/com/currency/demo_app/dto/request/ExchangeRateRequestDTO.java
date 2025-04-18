package com.currency.demo_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateRequestDTO {
    @NotBlank(message = "Source currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Invalid source currency code")
    private String sourceCurrency;

    @NotBlank(message = "Target currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Invalid target currency code")
    private String targetCurrency;
    private boolean useExternalApi;
}