package com.company.payment_system.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetryPaymentResponse {

    private String transactionId;

    private String previousStatus;

    private String currentStatus;

    private Integer retryCount;

    private String message;

    private LocalDateTime retryTime;
}