package com.lms.www.affiliate.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class AffiliateStatsResponse {
    private long totalClicks;
    private long totalSales;
    private double conversionRate;
    private BigDecimal totalRevenue;
    private BigDecimal totalCommission;
}
