package com.company.payment_system.service.impl;

import com.company.payment_system.dto.*;
import com.company.payment_system.entity.PaymentTransaction;
import com.company.payment_system.enums.PaymentStatus;
import com.company.payment_system.repository.PaymentTransactionRepository;
import com.company.payment_system.service.PaymentService;
import com.company.payment_system.util.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.company.payment_system.exception.InvalidPaymentStateException;
import java.time.LocalDateTime;
import com.company.payment_system.exception.PaymentNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentTransactionRepository repository;

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

        repository.save(transaction);

        return PaymentResponse.builder()
                .transactionId(transactionId)
                .status(PaymentStatus.PENDING.name())
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

}