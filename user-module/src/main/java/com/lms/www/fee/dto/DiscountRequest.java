package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest {
    private String type; // FIXED, PERCENTAGE
    private BigDecimal value;
    private String reason;
    private Long userId;
    private Long feeStructureId;
    private String discountName;
    private BigDecimal amount;
    private String scope;
    private Long scopeId;
}
