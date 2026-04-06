package com.lms.www.affiliate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lms.www.affiliate.entity.CommissionType;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAffiliateRequest {
    private Long userId;
    private String name;
    private String username;
    private String email;
    private String mobile;
    private String password;
    private CommissionType commissionType;
    private Double commissionValue;
    private Integer cookieDays;
    private String type;
}
