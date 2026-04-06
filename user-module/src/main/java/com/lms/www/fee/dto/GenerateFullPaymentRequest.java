package com.lms.www.fee.dto;

import com.lms.www.fee.payment.entity.EarlyPayment;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GenerateFullPaymentRequest {
    private Long allocationId;
    private EarlyPayment.DiscountType discountType;
    private BigDecimal discountValue;
}