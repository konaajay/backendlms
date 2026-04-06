package com.lms.www.affiliate.dto;

import java.math.BigDecimal;

import com.lms.www.affiliate.entity.AffiliateStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AffiliateAdminResponse {
    private Long id;
    private String name;
    private String email;
    private String mobile;
    private String type;
    private String code;
    private AffiliateStatus status;
    private Long totalLeads;
    private Long conversions;
    private Long totalClicks; // Track clicks/views
    private BigDecimal totalRevenue;
    private BigDecimal totalEarned;

    // Bank Details
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
    private String upiId;

    // Manual accessors and builder fallback
    public void setStatus(AffiliateStatus status) { this.status = status; }
    public AffiliateStatus getStatus() { return status; }
}
