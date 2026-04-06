package com.lms.www.fee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkPaymentResponse {
    private Long paymentId;
    private String paymentSessionId;
    private String orderId;
}
