package com.lms.www.fee.dto;

import com.lms.www.fee.payment.entity.EarlyPayment;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GenerateEarlyPaymentRequest {
    private List<Long> installmentIds;
    private EarlyPayment.DiscountType discountType;
    private BigDecimal discountValue;
}