package com.company.payment_system.dto;

import com.company.payment_system.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryResponse {

    private String transactionId;

    private BigDecimal amount;

    private String currency;

    private String paymentMethod;

    private PaymentStatus status;

    private String customerEmail;

    private Integer retryCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}