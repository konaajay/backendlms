package com.lms.www.controller.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AffiliateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String mobile;
    private String commissionType;
    private Double commissionValue;

    // ✅ REQUIRED
    private String roleName;
    
    private List<String> permissions;
}
