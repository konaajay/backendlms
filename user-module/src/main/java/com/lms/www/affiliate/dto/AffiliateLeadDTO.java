package com.lms.www.affiliate.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class AffiliateLeadDTO {
    private Long id;
    private String name;
    private String email;
    private String mobile;
    private Long courseId;
    private Long batchId;
    private Long affiliateId;
    private String affiliateName;
    private String referralCode;
    private Long linkId;
    private String status;
    private String leadSource;
    private String ipAddress;
    private LocalDateTime createdAt;
    private BigDecimal studentDiscountValue;

    public AffiliateLeadDTO() {}

    // Manual Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long id) { this.courseId = id; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long id) { this.batchId = id; }
    public Long getAffiliateId() { return affiliateId; }
    public void setAffiliateId(Long id) { this.affiliateId = id; }
    public String getAffiliateName() { return affiliateName; }
    public void setAffiliateName(String name) { this.affiliateName = name; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String code) { this.referralCode = code; }
    public Long getLinkId() { return linkId; }
    public void setLinkId(Long id) { this.linkId = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLeadSource() { return leadSource; }
    public void setLeadSource(String source) { this.leadSource = source; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ip) { this.ipAddress = ip; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public java.math.BigDecimal getStudentDiscountValue() { return studentDiscountValue; }
    public void setStudentDiscountValue(java.math.BigDecimal val) { this.studentDiscountValue = val; }

    // Manual Builder
    public static AffiliateLeadDTOBuilder builder() {
        return new AffiliateLeadDTOBuilder();
    }

    public static class AffiliateLeadDTOBuilder {
        private AffiliateLeadDTO dto = new AffiliateLeadDTO();
        public AffiliateLeadDTOBuilder id(Long id) { dto.id = id; return this; }
        public AffiliateLeadDTOBuilder name(String n) { dto.name = n; return this; }
        public AffiliateLeadDTOBuilder email(String e) { dto.email = e; return this; }
        public AffiliateLeadDTOBuilder mobile(String m) { dto.mobile = m; return this; }
        public AffiliateLeadDTOBuilder courseId(Long id) { dto.courseId = id; return this; }
        public AffiliateLeadDTOBuilder batchId(Long id) { dto.batchId = id; return this; }
        public AffiliateLeadDTOBuilder affiliateId(Long id) { dto.affiliateId = id; return this; }
        public AffiliateLeadDTOBuilder affiliateName(String n) { dto.affiliateName = n; return this; }
        public AffiliateLeadDTOBuilder referralCode(String c) { dto.referralCode = c; return this; }
        public AffiliateLeadDTOBuilder linkId(Long id) { dto.linkId = id; return this; }
        public AffiliateLeadDTOBuilder status(String s) { dto.status = s; return this; }
        public AffiliateLeadDTOBuilder leadSource(String s) { dto.leadSource = s; return this; }
        public AffiliateLeadDTOBuilder ipAddress(String i) { dto.ipAddress = i; return this; }
        public AffiliateLeadDTOBuilder createdAt(LocalDateTime d) { dto.createdAt = d; return this; }
        public AffiliateLeadDTOBuilder studentDiscountValue(java.math.BigDecimal v) { dto.studentDiscountValue = v; return this; }
        public AffiliateLeadDTO build() { return dto; }
    }
}
