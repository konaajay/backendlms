package com.lms.www.fee.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class EarlyPaymentRequest {
    private Long studentId;
    private List<Long> installmentIds;
    private String discountType;
    private BigDecimal discountValue;
}
