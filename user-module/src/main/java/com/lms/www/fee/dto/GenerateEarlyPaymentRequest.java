package com.lms.www.fee.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lms.www.fee.payment.entity.EarlyPayment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateEarlyPaymentRequest {
    private Long studentId;
    private List<Long> installmentIds;
    private EarlyPayment.DiscountType discountType;
    private BigDecimal discountValue;
}