package com.company.payment_system.util;

import java.util.UUID;

public class TransactionIdGenerator {

    private TransactionIdGenerator() {
    }

    public static String generateTransactionId() {
        return "TXN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}