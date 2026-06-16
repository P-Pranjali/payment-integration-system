package com.company.payment_system.service.impl;

import com.company.payment_system.dto.PaymentRequest;
import com.company.payment_system.dto.PaymentResponse;
import com.company.payment_system.entity.PaymentTransaction;
import com.company.payment_system.repository.PaymentTransactionRepository;
import com.company.payment_system.service.PaymentService;
import com.company.payment_system.util.TransactionIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.company.payment_system.dto.PaymentStatusResponse;
import com.company.payment_system.exception.PaymentNotFoundException;

@Service
@RequiredArgsConstructor
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
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        repository.save(transaction);

        return PaymentResponse.builder()
                .transactionId(transactionId)
                .status("PENDING")
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
                .status(transaction.getStatus())
                .customerEmail(transaction.getCustomerEmail())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}