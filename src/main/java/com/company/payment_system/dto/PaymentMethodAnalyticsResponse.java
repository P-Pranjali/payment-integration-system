package com.company.payment_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class PaymentMethodAnalyticsResponse {

    private String paymentMethod;

    private long transactionCount;

    private BigDecimal totalAmount;
}