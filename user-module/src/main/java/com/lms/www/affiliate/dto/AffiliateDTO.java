package com.lms.www.affiliate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AffiliateDTO {
    private Long id;
    private Long userId;
    private String type;
    private String code;
    private String referralCode;
    private String name;
    private String username;
    private String email;
    private String mobile;
    private String status;
    private String commissionType;
    private BigDecimal commissionValue;
    private BigDecimal studentDiscountValue;
    private Integer cookieDays;
    private BigDecimal minPayout;
    private boolean withdrawalEnabled;
    private LocalDateTime createdAt;

    public AffiliateDTO() {}

    // Manual Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long id) { this.userId = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String code) { this.referralCode = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCommissionType() { return commissionType; }
    public void setCommissionType(String type) { this.commissionType = type; }
    public BigDecimal getCommissionValue() { return commissionValue; }
    public void setCommissionValue(BigDecimal value) { this.commissionValue = value; }
    public BigDecimal getStudentDiscountValue() { return studentDiscountValue; }
    public void setStudentDiscountValue(BigDecimal value) { this.studentDiscountValue = value; }
    public Integer getCookieDays() { return cookieDays; }
    public void setCookieDays(Integer days) { this.cookieDays = days; }
    public BigDecimal getMinPayout() { return minPayout; }
    public void setMinPayout(BigDecimal min) { this.minPayout = min; }
    public boolean isWithdrawalEnabled() { return withdrawalEnabled; }
    public void setWithdrawalEnabled(boolean enabled) { this.withdrawalEnabled = enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }

    // Manual Builder
    public static AffiliateDTOBuilder builder() {
        return new AffiliateDTOBuilder();
    }

    public static class AffiliateDTOBuilder {
        private AffiliateDTO dto = new AffiliateDTO();
        public AffiliateDTOBuilder id(Long id) { dto.id = id; return this; }
        public AffiliateDTOBuilder userId(Long id) { dto.userId = id; return this; }
        public AffiliateDTOBuilder type(String t) { dto.type = t; return this; }
        public AffiliateDTOBuilder code(String c) { dto.code = c; return this; }
        public AffiliateDTOBuilder referralCode(String c) { dto.referralCode = c; return this; }
        public AffiliateDTOBuilder name(String n) { dto.name = n; return this; }
        public AffiliateDTOBuilder username(String u) { dto.username = u; return this; }
        public AffiliateDTOBuilder email(String e) { dto.email = e; return this; }
        public AffiliateDTOBuilder mobile(String m) { dto.mobile = m; return this; }
        public AffiliateDTOBuilder status(String s) { dto.status = s; return this; }
        public AffiliateDTOBuilder commissionType(String t) { dto.commissionType = t; return this; }
        public AffiliateDTOBuilder commissionValue(BigDecimal v) { dto.commissionValue = v; return this; }
        public AffiliateDTOBuilder studentDiscountValue(BigDecimal v) { dto.studentDiscountValue = v; return this; }
        public AffiliateDTOBuilder cookieDays(Integer d) { dto.cookieDays = d; return this; }
        public AffiliateDTOBuilder minPayout(BigDecimal m) { dto.minPayout = m; return this; }
        public AffiliateDTOBuilder withdrawalEnabled(boolean e) { dto.withdrawalEnabled = e; return this; }
        public AffiliateDTOBuilder createdAt(LocalDateTime d) { dto.createdAt = d; return this; }
        public AffiliateDTO build() { return dto; }
    }
}
