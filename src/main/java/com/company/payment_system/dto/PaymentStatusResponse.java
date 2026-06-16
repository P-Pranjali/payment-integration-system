package com.company.payment_system.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusResponse {

    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String status;
    private String customerEmail;
    private LocalDateTime createdAt;
}