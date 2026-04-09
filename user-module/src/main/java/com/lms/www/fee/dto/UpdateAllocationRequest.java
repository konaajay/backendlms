package com.lms.www.fee.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateAllocationRequest {

    private BigDecimal adminDiscount;
    private BigDecimal additionalDiscount;

    @Min(value = 1, message = "Installment count must be at least 1")
    @Max(value = 24, message = "Installment count cannot exceed 24")
    private Integer installmentCount;

    private BigDecimal promoDiscount;
    private String appliedPromoCode;
}