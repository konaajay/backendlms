package com.lms.www.affiliate.dto;

import com.lms.www.affiliate.entity.AffiliateStatus;
import com.lms.www.affiliate.entity.CommissionType;
import lombok.Data;

@Data
public class UpdateAffiliateSettingsRequest {
    private CommissionType commissionType;
    private Double commissionValue;
    private Double studentDiscountValue;
    private Integer cookieDays;
    private AffiliateStatus status;
}
