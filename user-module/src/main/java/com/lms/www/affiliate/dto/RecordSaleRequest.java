package com.lms.www.affiliate.dto;

import lombok.Data;

@Data
public class RecordSaleRequest {
    private String affiliateCode;
    private Long courseId;
    private Long batchId;
    private String orderId;
    private Double amount;
}
