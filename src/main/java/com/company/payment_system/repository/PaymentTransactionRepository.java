package com.company.payment_system.repository;

import com.company.payment_system.entity.PaymentTransaction;
import com.company.payment_system.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByTransactionId(String transactionId);

    Page<PaymentTransaction> findByStatus(
            PaymentStatus status,
            Pageable pageable);

    Page<PaymentTransaction> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable);

    long countByStatus(PaymentStatus status);

    long countByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate);

    long countByStatusAndCreatedAtBetween(
            PaymentStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate);

    List<PaymentTransaction> findAllByStatus(
            PaymentStatus status);

    List<PaymentTransaction> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate);
}
