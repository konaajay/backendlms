package com.lms.www.affiliate.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class AffiliateLinkDTO {
    private Long id;
    private Long affiliateId;
    private Long courseId;
    private Long batchId;
    private String course;
    private String batch;
    private String code; // Keep for compatibility
    private String referralCode;
    private BigDecimal commissionValue;
    private BigDecimal studentDiscountValue;
    private Long clicks;
    private Long leads;
    private Long conversions;
    private BigDecimal earnings;
    private String status;
    private LocalDateTime createdAt;

    public AffiliateLinkDTO() {}

    public AffiliateLinkDTO(Long id, Long affiliateId, Long courseId, Long batchId, String course, String batch, String code, String referralCode, BigDecimal commissionValue, BigDecimal studentDiscountValue, Long clicks, Long leads, Long conversions, BigDecimal earnings, String status, LocalDateTime createdAt) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.courseId = courseId;
        this.batchId = batchId;
        this.course = course;
        this.batch = batch;
        this.code = code;
        this.referralCode = referralCode;
        this.commissionValue = commissionValue;
        this.studentDiscountValue = studentDiscountValue;
        this.clicks = clicks;
        this.leads = leads;
        this.conversions = conversions;
        this.earnings = earnings;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Manual Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAffiliateId() { return affiliateId; }
    public void setAffiliateId(Long affiliateId) { this.affiliateId = affiliateId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public BigDecimal getCommissionValue() { return commissionValue; }
    public void setCommissionValue(BigDecimal commissionValue) { this.commissionValue = commissionValue; }

    public BigDecimal getStudentDiscountValue() { return studentDiscountValue; }
    public void setStudentDiscountValue(BigDecimal studentDiscountValue) { this.studentDiscountValue = studentDiscountValue; }

    public Long getClicks() { return clicks; }
    public void setClicks(Long clicks) { this.clicks = clicks; }

    public Long getLeads() { return leads; }
    public void setLeads(Long leads) { this.leads = leads; }

    public Long getConversions() { return conversions; }
    public void setConversions(Long conversions) { this.conversions = conversions; }

    public BigDecimal getEarnings() { return earnings; }
    public void setEarnings(BigDecimal earnings) { this.earnings = earnings; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Manual Builder Pattern
    public static AffiliateLinkDTOBuilder builder() {
        return new AffiliateLinkDTOBuilder();
    }

    public static class AffiliateLinkDTOBuilder {
        private Long id;
        private Long affiliateId;
        private Long courseId;
        private Long batchId;
        private String course;
        private String batch;
        private String code;
        private String referralCode;
        private BigDecimal commissionValue;
        private BigDecimal studentDiscountValue;
        private Long clicks;
        private Long leads;
        private Long conversions;
        private BigDecimal earnings;
        private String status;
        private LocalDateTime createdAt;

        public AffiliateLinkDTOBuilder id(Long id) { this.id = id; return this; }
        public AffiliateLinkDTOBuilder affiliateId(Long affiliateId) { this.affiliateId = affiliateId; return this; }
        public AffiliateLinkDTOBuilder courseId(Long courseId) { this.courseId = courseId; return this; }
        public AffiliateLinkDTOBuilder batchId(Long batchId) { this.batchId = batchId; return this; }
        public AffiliateLinkDTOBuilder course(String course) { this.course = course; return this; }
        public AffiliateLinkDTOBuilder batch(String batch) { this.batch = batch; return this; }
        public AffiliateLinkDTOBuilder code(String code) { this.code = code; return this; }
        public AffiliateLinkDTOBuilder referralCode(String referralCode) { this.referralCode = referralCode; return this; }
        public AffiliateLinkDTOBuilder commissionValue(BigDecimal commissionValue) { this.commissionValue = commissionValue; return this; }
        public AffiliateLinkDTOBuilder studentDiscountValue(BigDecimal studentDiscountValue) { this.studentDiscountValue = studentDiscountValue; return this; }
        public AffiliateLinkDTOBuilder clicks(Long clicks) { this.clicks = clicks; return this; }
        public AffiliateLinkDTOBuilder leads(Long leads) { this.leads = leads; return this; }
        public AffiliateLinkDTOBuilder conversions(Long conversions) { this.conversions = conversions; return this; }
        public AffiliateLinkDTOBuilder earnings(BigDecimal earnings) { this.earnings = earnings; return this; }
        public AffiliateLinkDTOBuilder status(String status) { this.status = status; return this; }
        public AffiliateLinkDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AffiliateLinkDTO build() {
            return new AffiliateLinkDTO(id, affiliateId, courseId, batchId, course, batch, code, referralCode, commissionValue, studentDiscountValue, clicks, leads, conversions, earnings, status, createdAt);
        }
    }
}
