package com.lms.www.affiliate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lms.www.affiliate.entity.AffiliateSale.SaleStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AffiliateSaleDTO {
    private String orderId;
    private Long courseId;
    private Long batchId;
    private BigDecimal orderAmount;
    private BigDecimal commissionAmount;
    private SaleStatus status;
    private LocalDateTime createdAt;
}
