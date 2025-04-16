package com.currency.demo_app.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CurrencyConversionFilterRequestDTO {
    private String transactionId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    
    private int page = 0;
    private int size = 10;
}