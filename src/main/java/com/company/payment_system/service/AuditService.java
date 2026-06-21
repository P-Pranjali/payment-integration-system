package com.company.payment_system.service;

import com.company.payment_system.dto.PaymentAuditResponse;
import com.company.payment_system.enums.PaymentEventType;

import java.util.List;

public interface AuditService {

    void logEvent(
            String transactionId,
            PaymentEventType eventType,
            String description);

    List<PaymentAuditResponse>
    getAuditTrail(String transactionId);
}