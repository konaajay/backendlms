package com.lms.www.affiliate.dto;

import lombok.Data;

@Data
public class RegisterAffiliateRequest {
    private Long userId;
    private String email;
    private String name;
    private String mobile;
}
