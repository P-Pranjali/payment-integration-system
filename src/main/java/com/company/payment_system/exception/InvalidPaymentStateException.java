package com.company.payment_system.exception;

public class InvalidPaymentStateException extends RuntimeException {

    public InvalidPaymentStateException(String status){

        super("Can not process payment. Current status: " + status);
    }
}
