package com.lms.www.affiliate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateMetricsResponse {
    private Long totalLeads;
    private Long convertedLeads;
    private Double conversionRate;
    private BigDecimal pendingCommission;
    private BigDecimal paidCommission;
    private BigDecimal totalEarned; // paid + pending
    private BigDecimal walletBalance;
    private BigDecimal totalRevenue;
}
