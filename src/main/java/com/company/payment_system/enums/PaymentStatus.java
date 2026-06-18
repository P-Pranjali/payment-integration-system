package com.company.payment_system.enums;

public enum PaymentStatus {
    PENDING(false),
    PROCESSING(false),
    HOLD(false),
    SUCCESS(true),
    FAILED(true),
    CANCELLED(true);

    private final boolean isFinal;

    PaymentStatus(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
