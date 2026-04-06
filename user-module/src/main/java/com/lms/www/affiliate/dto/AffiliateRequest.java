package com.lms.www.affiliate.dto;

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

    // ✅ REQUIRED
    private String roleName;

    private List<String> permissions;
}
