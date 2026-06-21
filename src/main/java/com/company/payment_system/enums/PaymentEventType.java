package com.company.payment_system.enums;

public enum PaymentEventType {

    PAYMENT_CREATED,
    PAYMENT_PROCESSING,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    PAYMENT_RETRY,
    WEBHOOK_RECEIVED,
    STATUS_UPDATED
}