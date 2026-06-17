package com.company.payment_system.service;

import com.company.payment_system.dto.PaymentRequest;
import com.company.payment_system.dto.PaymentResponse;
import com.company.payment_system.dto.PaymentStatusResponse;
import com.company.payment_system.dto.ProcessPaymentResponse;
import com.company.payment_system.enums.PaymentStatus;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);

    PaymentStatusResponse getPaymentByTransactionId(String transactionId);

    ProcessPaymentResponse processPayment(String transactionId, PaymentStatus forceStatus);
}
