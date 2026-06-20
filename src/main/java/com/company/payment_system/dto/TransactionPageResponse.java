package com.company.payment_system.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionPageResponse {

    private List<TransactionHistoryResponse> transactions;

    private long totalRecords;

    private int totalPages;

    private int currentPage;
}