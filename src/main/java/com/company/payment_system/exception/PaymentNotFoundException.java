package com.company.payment_system.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String transactionId) {
        super("Payment not found with transaction ID: " + transactionId);
    }
}