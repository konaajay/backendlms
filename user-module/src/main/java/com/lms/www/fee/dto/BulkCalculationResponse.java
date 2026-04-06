package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkCalculationResponse {
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal finalPayable;
}
