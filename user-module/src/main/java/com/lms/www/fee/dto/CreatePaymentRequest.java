package com.lms.www.fee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreatePaymentRequest {
    @NotNull
    private Long allocationId;
    
    private Long installmentId;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotBlank
    private String paymentMode;
    
    @NotBlank
    private String transactionReference;
    
    private String screenshotUrl;
    
    @NotNull
    private Long recordedBy;
    
    private String studentName;
    private String studentEmail;
    private List<Double> discountPercentages;
    private BigDecimal manualDiscount;
    private String remarks;
}
