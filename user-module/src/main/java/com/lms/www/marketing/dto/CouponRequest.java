package com.lms.www.marketing.dto;

import java.util.List;

import com.lms.www.marketing.model.DiscountType;

import lombok.Data;

@Data
public class CouponRequest {
    private String code;
    private DiscountType discountType;
    private Double discountValue;
    private Double discountCap;
    private String expiryDate; // String for easy parsing in service
    private Integer maxUsage;
    private Double minPurchaseAmount;
    private Integer perUserLimit;
    private boolean isFirstOrderOnly;
    private boolean autoApply;
    private Long affiliateId;
    private Long learnerId;
    private List<Long> courseIds;
}
