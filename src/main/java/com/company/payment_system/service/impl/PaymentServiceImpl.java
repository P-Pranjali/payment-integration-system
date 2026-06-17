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

import com.company.payment_system.enums.PaymentStatus;
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

        if(transaction.getStatus() != PaymentStatus.PENDING){

            log.warn(
                    "Invalid status transition attempted for transaction {}. Current status: {}",
                    transaction.getTransactionId(),
                    transaction.getStatus());


            throw new InvalidPaymentStateException(
                    transaction.getStatus().name()
            );
        }

        transaction.setStatus(request.getPaymentStatus());
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
}