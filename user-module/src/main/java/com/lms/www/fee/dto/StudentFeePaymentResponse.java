package com.lms.www.fee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeePaymentResponse {
    private Long paymentId;
    private Long allocationId;
    private Long installmentId;
    private BigDecimal paidAmount;
    private BigDecimal discountAmount;
    private LocalDateTime paymentDate;
    private String paymentMode;
    private String paymentStatus;
    private String transactionReference;
    private String screenshotUrl;
    private String currency;
    private Long recordedBy;

    private String studentName;
    private String studentEmail;
}
