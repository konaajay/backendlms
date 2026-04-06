package com.lms.www.affiliate.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class WithdrawalRequest {
    private Long userId;
    private BigDecimal amount;
}
