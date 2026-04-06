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
public class RefundRuleResponse {
    private Long id;
    private String name;
    private Integer daysBeforeStart;
    private BigDecimal refundPercentage;
    private Boolean active;
}
