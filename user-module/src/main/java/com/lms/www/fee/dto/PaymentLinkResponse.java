package com.lms.www.fee.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentLinkResponse {
    private Long installmentId;
    private String paymentLink;
    private Boolean success;
}