package com.lms.www.fee.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAllocationRequest {

    private Long userId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long feeStructureId;
    private Long batchId;
    private Long courseId;
    
    private BigDecimal adminDiscount;
    private BigDecimal additionalDiscount;
    private BigDecimal advancePayment;
    private String courseName;
    private String batchName;

    @Min(value = 1, message = "Installment count must be at least 1")
    @Max(value = 24, message = "Installment count cannot exceed 24")
    private Integer installmentCount;

    private String appliedPromoCode;
    private BigDecimal promoDiscount;
    private BigDecimal affiliateDiscount;

    // Financial totals if needed by backend
    private BigDecimal originalAmount;
    private BigDecimal gstRate;
    private BigDecimal payableAmount;
    private BigDecimal remainingAmount;
    private BigDecimal paidAmount;
    private String status;
    private String currency;
}