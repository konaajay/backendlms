package com.lms.www.fee.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ManualPaymentRequest {
    private Long allocationId;
    private Long installmentPlanId;
    private BigDecimal amount;
    private String paymentMode;
    private String transactionRef;
    private String screenshotUrl;
    private Long recordedBy;
    private String studentName;
    private String studentEmail;
    private List<Double> discountPercentages;
    private BigDecimal manualDiscount;
    private String remarks;
}
