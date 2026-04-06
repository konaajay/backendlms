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
public class InstallmentPaymentResponse {
    private String orderId;
    private BigDecimal amount;
    private String paymentSessionId;
    private String studentName;
    private String status;
    private String label;
}
