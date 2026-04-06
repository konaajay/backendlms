package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeDashboardResponse {
    private BigDecimal totalAllocated;
    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private NextInstallment nextInstallment;
    private List<RecentTransaction> recentTransactions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NextInstallment {
        private BigDecimal amount;
        private LocalDate dueDate;
        private String label;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTransaction {
        private Long id;
        private BigDecimal amount;
        private LocalDate date;
        private String status;
        private String mode;
    }
}
