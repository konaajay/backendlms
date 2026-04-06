package com.lms.www.affiliate.dto;

import java.math.BigDecimal;
import com.lms.www.affiliate.entity.CommissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAffiliateResponse {
    private Long id;
    private Long userId;
    private String name;
    private String code;
    private String referralCode;
    private BigDecimal commissionValue;
    private CommissionType commissionType;
    private String status;
}
