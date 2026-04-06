package com.lms.www.fee.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeeDiscountRequest {
    private Long userId;
    private BigDecimal amount;
    private String reason;
}
