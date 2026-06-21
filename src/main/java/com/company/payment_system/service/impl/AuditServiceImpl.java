package com.company.payment_system.service.impl;

import com.company.payment_system.dto.PaymentAuditResponse;
import com.company.payment_system.entity.PaymentAuditLog;
import com.company.payment_system.enums.PaymentEventType;
import com.company.payment_system.repository.PaymentAuditLogRepository;
import com.company.payment_system.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final PaymentAuditLogRepository repository;

    @Override
    public void logEvent(
            String transactionId,
            PaymentEventType eventType,
            String description) {

        repository.save(
                PaymentAuditLog.builder()
                        .transactionId(transactionId)
                        .eventType(eventType)
                        .description(description)
                        .createdAt(LocalDateTime.now())
                        .build());
    }

    @Override
    public List<PaymentAuditResponse>
    getAuditTrail(String transactionId) {

        return repository
                .findByTransactionIdOrderByCreatedAtAsc(transactionId)
                .stream()
                .map(log ->
                        PaymentAuditResponse.builder()
                                .eventType(log.getEventType())
                                .description(log.getDescription())
                                .createdAt(log.getCreatedAt())
                                .build())
                .toList();
    }
}