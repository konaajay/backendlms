package com.lms.www.marketing.dto;

import lombok.Data;

@Data
public class CouponApplyRequest {
    private String code;
    private Long courseId;
    private Double amount;
    private Long learnerId; // Optional: can be null
}
