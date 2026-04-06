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
public class AllocationResponse {
    private Long id;
    private Long userId;
    private String structureName;
    private BigDecimal totalAmount;
    private BigDecimal payableAmount;
    private BigDecimal remainingAmount;
    private String status;
}
