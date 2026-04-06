package com.lms.www.affiliate.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AffiliateDashboardResponse {
    private String referralCode;
    private long totalReferrals; // Leads
    private long totalClicks;
    private long purchases; // Converted leads
    private BigDecimal totalEarnings;
    private BigDecimal totalRevenue;
    private BigDecimal walletBalance;
    
    // Detailed profile
    private String name;
    private String email;
    private String status;
    
    // Activity
    private List<AffiliateLeadDTO> recentActivity;
    private List<AffiliateLinkDTO> activeLinks;

    // Bank Details
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
    private String upiId;
}
