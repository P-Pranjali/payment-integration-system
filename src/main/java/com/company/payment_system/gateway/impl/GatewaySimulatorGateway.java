package com.company.payment_system.gateway.impl;

import com.company.payment_system.dto.PaymentRequest;
import com.company.payment_system.exception.GatewayException;
import com.company.payment_system.gateway.PaymentGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
//@Slf4j
public class GatewaySimulatorGateway implements PaymentGateway {

    private static final Logger log =
            LoggerFactory.getLogger(
                    GatewaySimulatorGateway.class);

    private final Random random = new Random();


    @Override
    @Retry(name = "paymentGateway")
    @CircuitBreaker(
            name = "paymentGateway",
            fallbackMethod = "gatewayFallback"
    )
    public String processPayment(PaymentRequest request) {

        int result = random.nextInt(100);

        if (result < 70) {

            log.info(
                    "Gateway processed payment successfully");

            return "SUCCESS";
        }

        log.error(
                "Gateway temporary failure");


        throw new GatewayException(
                "Temporary gateway failure");
    }


    private String gatewayFallback(
            PaymentRequest request,
            Throwable throwable) {

        if (throwable.getMessage().contains("OPEN")) {

            log.error(
                    "Circuit Breaker OPEN. Request blocked.");

        } else {

            log.error(
                    "Gateway call failed. Reason={}",
                    throwable.getMessage());
        }

        throw new GatewayException(
                "Payment gateway currently unavailable");
    }
}