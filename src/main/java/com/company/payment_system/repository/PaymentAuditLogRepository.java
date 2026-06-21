package com.company.payment_system.repository;

import com.company.payment_system.entity.PaymentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentAuditLogRepository
        extends JpaRepository<PaymentAuditLog, Long> {

    List<PaymentAuditLog> findByTransactionIdOrderByCreatedAtAsc(
            String transactionId);
}