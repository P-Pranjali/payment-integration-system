package com.company.payment_system.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentResponse {

    private String transactionId;
    private String status;
    private String message;
    private LocalDateTime processedAt;
}