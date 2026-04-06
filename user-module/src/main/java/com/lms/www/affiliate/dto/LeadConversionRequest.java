package com.lms.www.affiliate.dto;

import lombok.Data;

@Data
public class LeadConversionRequest {
    private Long studentId;
    private java.math.BigDecimal batchPrice;
}
