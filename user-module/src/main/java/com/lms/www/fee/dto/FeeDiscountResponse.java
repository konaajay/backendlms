package com.lms.www.fee.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FeeDiscountResponse {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String reason;
}
