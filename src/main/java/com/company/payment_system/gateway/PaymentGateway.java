package com.company.payment_system.gateway;

import com.company.payment_system.dto.PaymentRequest;

public interface PaymentGateway {

    String processPayment(PaymentRequest request);
}