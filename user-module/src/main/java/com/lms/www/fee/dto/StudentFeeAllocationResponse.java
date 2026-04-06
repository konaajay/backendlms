package com.lms.www.fee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StudentFeeAllocationResponse {
    private Long allocationId;
    private Long userId;
    private Long feeStructureId;
    private String feeStructureName;
    private BigDecimal originalAmount;
    private BigDecimal totalDiscount;
    private BigDecimal payableAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private BigDecimal oneTimeAmount;
    private BigDecimal installmentAmount;
    private String currency;
    private String status;
    private LocalDateTime allocationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String studentName;
    private String studentEmail;
    private Long batchId;
    private Long courseId;
    private String courseName;
    private String batchName;

    private String appliedPromoCode;
    private BigDecimal promoDiscount;
    private BigDecimal affiliateDiscount;
    private Long affiliateId;
}
