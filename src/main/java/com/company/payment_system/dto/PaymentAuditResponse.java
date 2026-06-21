package com.company.payment_system.dto;

import com.company.payment_system.enums.PaymentEventType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAuditResponse {

    private PaymentEventType eventType;

    private String description;

    private LocalDateTime createdAt;
}