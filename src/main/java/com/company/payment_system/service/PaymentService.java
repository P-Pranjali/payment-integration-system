package com.company.payment_system.service;

import com.company.payment_system.dto.*;
import com.company.payment_system.enums.PaymentStatus;

public interface PaymentService {

    PaymentResponse initiatePayment(PaymentRequest request);

    PaymentStatusResponse getPaymentByTransactionId(String transactionId);

    WebhookResponse updatePaymentStatus(WebhookRequest request);
}
