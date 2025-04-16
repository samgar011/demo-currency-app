package com.currency.demo_app.model;

import jakarta.persistence.*;
import lombok.*;
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
    private Double sourceAmount;

    @Column(nullable = false)
    private Double convertedAmount;

    @Column(nullable = false)
    private Double exchangeRate;

    private LocalDateTime createdAt;
}