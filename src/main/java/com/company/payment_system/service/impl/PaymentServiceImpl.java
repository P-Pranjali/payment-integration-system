package com.company.payment_system.service.impl;

import com.company.payment_system.dto.*;
import com.company.payment_system.entity.PaymentTransaction;
import com.company.payment_system.enums.PaymentEventType;
import com.company.payment_system.enums.PaymentStatus;
import com.company.payment_system.exception.GatewayException;
import com.company.payment_system.gateway.PaymentGateway;
import com.company.payment_system.repository.PaymentTransactionRepository;
import com.company.payment_system.service.AuditService;
import com.company.payment_system.service.PaymentService;
import com.company.payment_system.util.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.company.payment_system.exception.InvalidPaymentStateException;

import java.math.BigDecimal;
import java.time.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.company.payment_system.exception.PaymentNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository repository;

    private final PaymentGateway paymentGateway;

    private final AuditService auditService;

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) {

        String transactionId = TransactionIdGenerator.generateTransactionId();

        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionId(transactionId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .customerEmail(request.getCustomerEmail())
                .status(PaymentStatus.PENDING)
                .retryCount(0)
                .maxRetryCount(3)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        //repository.save(transaction);

        auditService.logEvent(
                transactionId,
                PaymentEventType.PAYMENT_CREATED,
                "Payment request received");



        try{
            String gatewayResult =
                    paymentGateway.processPayment(request);

            auditService.logEvent(
                    transactionId,
                    PaymentEventType.PAYMENT_PROCESSING,
                    "Payment sent to gateway");

            transaction.setStatus(
                    PaymentStatus.valueOf(gatewayResult)
            );

            auditService.logEvent(
                    transactionId,
                    PaymentEventType.PAYMENT_SUCCESS,
                    "Payment completed successfully");

        }catch(GatewayException e){

            log.error(
                    "Gateway processing Failed for transaction = {} ",
                    transactionId);

            transaction.setStatus((PaymentStatus.FAILED));

            auditService.logEvent(
                    transactionId,
                    PaymentEventType.PAYMENT_FAILED,
                    "Gateway processing failed");

        }
        transaction.setUpdatedAt(LocalDateTime.now());

        repository.save(transaction);

        return PaymentResponse.builder()
                .transactionId(transactionId)
                .status(transaction.getStatus().name())
                .message("Payment initiated successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }
    @Override
    public PaymentStatusResponse getPaymentByTransactionId(String transactionId) {

        PaymentTransaction transaction = repository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(transactionId));

        return PaymentStatusResponse.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus().name())
                .retryCount(transaction.getRetryCount())
                .maxRetryCount(transaction.getMaxRetryCount())
                .customerEmail(transaction.getCustomerEmail())
                .createdAt(transaction.getCreatedAt())
                .build();
    }


    @Override
    public WebhookResponse updatePaymentStatus(WebhookRequest request) {

        auditService.logEvent(
                request.getTransactionId(),
                PaymentEventType.WEBHOOK_RECEIVED,
                "Webhook received with status "
                        + request.getPaymentStatus());

        log.info("Webhook received for transactionId={}, requestedStatus={}",
                request.getTransactionId(),
                request.getPaymentStatus());

        PaymentTransaction transaction = repository
                .findByTransactionId(request.getTransactionId())
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                request.getTransactionId()));

        log.info("Current status of transaction {} is {}",
                transaction.getTransactionId(),
                transaction.getStatus());

        //If webhook is repeating SAME status → IGNORE (idempotency)
        if (transaction.getStatus() == request.getPaymentStatus()){
            return WebhookResponse.builder()
                    .transactionId(transaction.getTransactionId())
                    .status(transaction.getStatus().name())
                    .message("Duplicate webhook ignored (idempotent processing)")
                    .updatedAt(transaction.getUpdatedAt())
                    .build();
        }

        //If transaction already in FINAL state → BLOCK
        if(transaction.getStatus().isFinal()){

            log.warn(
                    "Invalid status transition attempted for transaction {}. Current status: {}",
                    transaction.getTransactionId(),
                    transaction.getStatus());


            throw new InvalidPaymentStateException(
                   "Transaction already finalized: " +  transaction.getStatus()
            );
        }


        transaction.setStatus(request.getPaymentStatus());
        if(request.getPaymentStatus() == PaymentStatus.FAILED){
            log.info(
                    "Transaction {} moved to FAILED state. retry count = {} ",
                    transaction.getTransactionId(),
                    transaction.getRetryCount()
            );
        }
        transaction.setUpdatedAt(LocalDateTime.now());

        auditService.logEvent(
                transaction.getTransactionId(),
                PaymentEventType.STATUS_UPDATED,
                "Status changed to "
                        + request.getPaymentStatus());

        repository.save(transaction);

        log.info(
                "Payment status updated successfully. transactionId={}, newStatus={}",
                transaction.getTransactionId(),
                transaction.getStatus());

        return WebhookResponse.builder()
                .transactionId(transaction.getTransactionId())
                .status(transaction.getStatus().name())
                .message("Payment status updated successfully")
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    @Override
    public RetryPaymentResponse retryPayment(String transactionId) {

        PaymentTransaction transaction = repository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(transactionId));

        log.info(
                "Retry requested for transaction {}",
                transactionId);

        if (transaction.getStatus() != PaymentStatus.FAILED) {

            log.warn(
                    "Retry rejected for transaction {} because current status is {}",
                    transactionId,
                    transaction.getStatus());

            throw new InvalidPaymentStateException(
                    transaction.getStatus().name()+
                    ". Retry allowed only for FAILED transactions");
        }

        if (transaction.getRetryCount() >= transaction.getMaxRetryCount()) {

            log.warn(
                    "Maximum retry limit exceeded for transaction {}",
                    transactionId);

            throw new InvalidPaymentStateException(
                    "Maximum retry limit exceeded");
        }

        PaymentStatus previousStatus = transaction.getStatus();
        Integer currentRetryCount = transaction.getRetryCount() == null
                ?0
                : transaction.getRetryCount();

        transaction.setRetryCount(
                transaction.getRetryCount() + 1);

        auditService.logEvent(
                transaction.getTransactionId(),
                PaymentEventType.PAYMENT_RETRY,
                "Retry attempt #"
                        + transaction.getRetryCount());

        transaction.setStatus(PaymentStatus.PROCESSING);

        transaction.setLastRetryAt(LocalDateTime.now());

        transaction.setUpdatedAt(LocalDateTime.now());

        repository.save(transaction);

        log.info(
                "Retry initiated successfully. transactionId={}, retryCount={}",
                transaction.getTransactionId(),
                transaction.getRetryCount());

        return RetryPaymentResponse.builder()
                .transactionId(transaction.getTransactionId())
                .previousStatus(previousStatus.name())
                .currentStatus(transaction.getStatus().name())
                .retryCount(transaction.getRetryCount())
                .message("Payment retry initiated successfully")
                .retryTime(transaction.getLastRetryAt())
                .build();
    }

    @Override
    public TransactionPageResponse getTransactionHistory(
            PaymentStatus status,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PaymentTransaction> transactions;

        if (status != null) {

            transactions =
                    repository.findByStatus(status, pageable);

        } else if (startDate != null && endDate != null) {

            transactions =
                    repository.findByCreatedAtBetween(
                            startDate.atStartOfDay(),
                            endDate.atTime(LocalTime.MAX),
                            pageable);

        } else {

            transactions =
                    repository.findAll(pageable);
        }

        return TransactionPageResponse.builder()
                .transactions(
                        transactions.getContent()
                                .stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList()))
                .totalRecords(transactions.getTotalElements())
                .totalPages(transactions.getTotalPages())
                .currentPage(transactions.getNumber())
                .build();
    }

    private TransactionHistoryResponse mapToResponse(
            PaymentTransaction transaction) {

        return TransactionHistoryResponse.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .customerEmail(transaction.getCustomerEmail())
                .retryCount(transaction.getRetryCount())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    private double calculateSuccessRate(
            long success,
            long total) {

        if (total == 0) {
            return 0;
        }

        double percentage =
                ((double) success / total) * 100;

        return Math.round(percentage * 100) / 100.0;
    }

    @Override
    public AnalyticsSummaryResponse getAnalyticsSummary() {

        long totalTransactions = repository.count();

        long success =
                repository.countByStatus(PaymentStatus.SUCCESS);

        long failed =
                repository.countByStatus(PaymentStatus.FAILED);

        long pending =
                repository.countByStatus(PaymentStatus.PENDING);

        BigDecimal totalProcessedAmount =
                repository.findAllByStatus(PaymentStatus.SUCCESS)
                        .stream()
                        .map(PaymentTransaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        double successRate =
                calculateSuccessRate(
                        success,
                        totalTransactions);


        return AnalyticsSummaryResponse.builder()
                .totalTransactions(totalTransactions)
                .successfulTransactions(success)
                .failedTransactions(failed)
                .pendingTransactions(pending)
                .successRate(successRate)
                .totalProcessedAmount(totalProcessedAmount)
                .build();
    }

    @Override
    public AnalyticsSummaryResponse getAnalyticsByDateRange(
            LocalDate startDate,
            LocalDate endDate) {

        LocalDateTime start =
                startDate.atStartOfDay();

        LocalDateTime end =
                endDate.atTime(LocalTime.MAX);

        long total =
                repository.countByCreatedAtBetween(start, end);

        long success =
                repository.countByStatusAndCreatedAtBetween(
                        PaymentStatus.SUCCESS,
                        start,
                        end);

        long failed =
                repository.countByStatusAndCreatedAtBetween(
                        PaymentStatus.FAILED,
                        start,
                        end);

        long pending =
                repository.countByStatusAndCreatedAtBetween(
                        PaymentStatus.PENDING,
                        start,
                        end);

        BigDecimal totalProcessedAmount =
                repository.findByCreatedAtBetween(start, end)
                        .stream()
                        .filter(t ->
                                t.getStatus() ==
                                        PaymentStatus.SUCCESS)
                        .map(PaymentTransaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);


        double successRate =
                calculateSuccessRate(
                        success,
                        total);

        return AnalyticsSummaryResponse.builder()
                .totalTransactions(total)
                .successfulTransactions(success)
                .failedTransactions(failed)
                .pendingTransactions(pending)
                .successRate(successRate)
                .totalProcessedAmount(totalProcessedAmount)
                .build();
    }

    @Override
    public List<PaymentMethodAnalyticsResponse>
    getPaymentMethodAnalytics() {

        List<PaymentTransaction> transactions =
                repository.findAll();

        Map<String, List<PaymentTransaction>> grouped =
                transactions.stream()
                        .collect(
                                Collectors.groupingBy(
                                        PaymentTransaction::getPaymentMethod));

        List<PaymentMethodAnalyticsResponse> response =
                new ArrayList<>();

        grouped.forEach((method, txns) -> {

            BigDecimal totalAmount =
                    txns.stream()
                            .map(PaymentTransaction::getAmount)
                            .reduce(BigDecimal.ZERO,
                                    BigDecimal::add);

            response.add(
                    PaymentMethodAnalyticsResponse.builder()
                            .paymentMethod(method)
                            .transactionCount(txns.size())
                            .totalAmount(totalAmount)
                            .build());
        });

        return response;
    }

    @Override
    public List<PaymentAuditResponse>
    getAuditTrail(String transactionId) {

        return auditService.getAuditTrail(transactionId);
    }
}