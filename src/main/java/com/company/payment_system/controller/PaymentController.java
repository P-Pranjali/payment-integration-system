package com.company.payment_system.controller;

import com.company.payment_system.dto.*;
import com.company.payment_system.enums.PaymentStatus;
import com.company.payment_system.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @PostMapping("/webhook")
    public ResponseEntity<WebhookResponse> updatePaymentStatus(
            @Valid  @RequestBody WebhookRequest request
    ){
        WebhookResponse response = paymentService.updatePaymentStatus(request);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/{transactionId}/retry")
    public ResponseEntity<RetryPaymentResponse> retryPayment(
            @PathVariable String transactionId) {

        RetryPaymentResponse response =
                paymentService.retryPayment(transactionId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<TransactionPageResponse> getTransactionHistory(

            @RequestParam(required = false)
            PaymentStatus status,

            @RequestParam(required = false)
            LocalDate startDate,

            @RequestParam(required = false)
            LocalDate endDate,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        TransactionPageResponse response =
                paymentService.getTransactionHistory(
                        status,
                        startDate,
                        endDate,
                        page,
                        size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity<AnalyticsSummaryResponse>
    getAnalyticsSummary() {

        return ResponseEntity.ok(
                paymentService.getAnalyticsSummary());
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsSummaryResponse>
    getAnalyticsByDateRange(

            @RequestParam LocalDate startDate,

            @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(
                paymentService.getAnalyticsByDateRange(
                        startDate,
                        endDate));
    }

    @GetMapping("/analytics/payment-methods")
    public ResponseEntity<
            List<PaymentMethodAnalyticsResponse>>
    getPaymentMethodAnalytics() {

        return ResponseEntity.ok(
                paymentService.getPaymentMethodAnalytics());
    }


}