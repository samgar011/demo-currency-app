package com.currency.demo_app.repository;


import com.currency.demo_app.model.CurrencyConversion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrencyConversionRepository extends JpaRepository<CurrencyConversion, Long> {
    CurrencyConversion findByTransactionId(String transactionId);

    Page<CurrencyConversion> findByTransactionId(String transactionId, Pageable pageable);

    Page<CurrencyConversion> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}