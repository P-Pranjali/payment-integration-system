package com.company.payment_system.exception;

public class GatewayException extends RuntimeException {

    public GatewayException(String message) {
        super(message);
    }
}