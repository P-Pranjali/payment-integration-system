package com.company.payment_system.service;

import com.company.payment_system.dto.*;
import com.company.payment_system.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);

    PaymentStatusResponse getPaymentByTransactionId(String transactionId);

    WebhookResponse updatePaymentStatus(WebhookRequest request);

    RetryPaymentResponse retryPayment(String transactionId);

    TransactionPageResponse getTransactionHistory(
            PaymentStatus status,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size);

    AnalyticsSummaryResponse getAnalyticsSummary();

    AnalyticsSummaryResponse getAnalyticsByDateRange(
            LocalDate startDate,
            LocalDate endDate);

    List<PaymentMethodAnalyticsResponse>
    getPaymentMethodAnalytics();

    List<PaymentAuditResponse>
    getAuditTrail(String transactionId);

}
