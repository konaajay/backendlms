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
public class StudentLedgerResponse {

    private AllocationSummary allocationSummary;
    private BigDecimal totalDue;
    private BigDecimal totalPaid;
    private BigDecimal remainingBalance;
    private List<InstallmentDto> installments;
    private List<StudentFeePaymentResponse> payments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AllocationSummary {
        private Long allocationId;
        private Long feeStructureId;
        private String feeStructureName;
        private String feeTypeName;
        private String studentName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstallmentDto {
        private Long id;
        private BigDecimal amount;
        private LocalDate dueDate;
        private String status;
        private boolean paymentLinkAvailable;
        private String orderId;
    }
}
