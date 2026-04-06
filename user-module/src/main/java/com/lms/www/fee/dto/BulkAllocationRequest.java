package com.lms.www.fee.dto;

import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BulkAllocationRequest {

    private List<Long> userIds;
    private Long feeStructureId;
    private BigDecimal adminDiscount;
    private BigDecimal additionalDiscount;
    private BigDecimal advancePayment;

    @Min(value = 1, message = "Installment count must be at least 1")
    @Max(value = 24, message = "Installment count cannot exceed 24")
    private Integer installmentCount;

    private String appliedPromoCode;
    private BigDecimal promoDiscount;
}