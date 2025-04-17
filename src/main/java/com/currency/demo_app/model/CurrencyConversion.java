package com.currency.demo_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "currency_conversions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyConversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private String sourceCurrency;

    @Column(nullable = false)
    private String targetCurrency;

    @Column(nullable = false)
    private BigDecimal sourceAmount;

    @Column(nullable = false)
    private BigDecimal convertedAmount;

    @Column(nullable = false)
    private BigDecimal exchangeRate;

    private LocalDateTime createdAt;
}