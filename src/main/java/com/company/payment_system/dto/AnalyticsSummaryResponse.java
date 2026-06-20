package com.company.payment_system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AnalyticsSummaryResponse {

    private long totalTransactions;

    private long successfulTransactions;

    private long failedTransactions;

    private long pendingTransactions;

    private double successRate;

    private BigDecimal totalProcessedAmount;
}