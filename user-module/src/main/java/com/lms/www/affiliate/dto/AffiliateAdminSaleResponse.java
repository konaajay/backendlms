package com.lms.www.affiliate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lms.www.affiliate.entity.AffiliateSale.SaleStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateAdminSaleResponse {
    private Long id;
    private String orderId;
    private Long affiliateId;
    private String affiliateName;
    private Long courseId;
    private Long batchId;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal orderAmount;
    private BigDecimal commissionAmount;
    private SaleStatus status;
    private LocalDateTime createdAt;
}
