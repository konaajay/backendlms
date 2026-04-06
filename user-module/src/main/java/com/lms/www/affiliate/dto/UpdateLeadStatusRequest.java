package com.lms.www.affiliate.dto;

import com.lms.www.affiliate.entity.AffiliateLead;

import lombok.Data;

@Data
public class UpdateLeadStatusRequest {
    private AffiliateLead.LeadStatus status;
    private String reason;
    private String changedBy;
}
