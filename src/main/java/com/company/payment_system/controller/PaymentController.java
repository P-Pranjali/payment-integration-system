package com.company.payment_system.controller;

import com.company.payment_system.dto.PaymentRequest;
import com.company.payment_system.dto.PaymentResponse;
import com.company.payment_system.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.company.payment_system.dto.PaymentStatusResponse;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response =
                paymentService.initiatePayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentStatusResponse> getPaymentStatus(
            @PathVariable String transactionId) {

        PaymentStatusResponse response =
                paymentService.getPaymentByTransactionId(transactionId);

        return ResponseEntity.ok(response);
    }
}