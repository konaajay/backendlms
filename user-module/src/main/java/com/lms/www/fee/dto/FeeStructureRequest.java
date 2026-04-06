package com.lms.www.fee.dto;

import java.math.BigDecimal;
import java.util.List;

public class FeeStructureRequest {
    private String name;
    private String academicYear;
    private Long courseId;
    private Long batchId;
    private String batchName;
    private String courseName;
    private BigDecimal baseAmount;
    private BigDecimal totalAmount;
    private Integer durationMonths;

    private Integer installmentCount;
    private Boolean gstApplicable;
    private BigDecimal gstPercent;

    private List<FeeComponentRequest> components;
    private List<FeeComponentRequest> breakdown;
    private List<FeePenaltySlabRequest> slabs;

    private BigDecimal admissionFeeAmount;
    private String discountType;
    private BigDecimal discountValue;
    private String penaltyType;
    private BigDecimal fixedPenaltyAmount;
    private BigDecimal penaltyPercentage;
    private BigDecimal maxPenaltyCap;
    private Long feeTypeId;
    private Boolean gstIncludedInFee;
    private Boolean isActive;

    public FeeStructureRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Alias for name to fix compilation in some mappers/services
    public String getFeeName() { return name; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getInstallmentCount() { return installmentCount; }
    public void setInstallmentCount(Integer installmentCount) { this.installmentCount = installmentCount; }

    public Boolean getGstApplicable() { return gstApplicable; }
    public void setGstApplicable(Boolean gstApplicable) { this.gstApplicable = gstApplicable; }

    public BigDecimal getGstPercent() { return gstPercent; }
    public void setGstPercent(BigDecimal gstPercent) { this.gstPercent = gstPercent; }

    public List<FeeComponentRequest> getComponents() { return components; }
    public void setComponents(List<FeeComponentRequest> components) { this.components = components; }

    public List<FeeComponentRequest> getBreakdown() { return breakdown; }
    public void setBreakdown(List<FeeComponentRequest> breakdown) { this.breakdown = breakdown; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

    public List<FeePenaltySlabRequest> getSlabs() { return slabs; }
    public void setSlabs(List<FeePenaltySlabRequest> slabs) { this.slabs = slabs; }

    public BigDecimal getAdmissionFeeAmount() { return admissionFeeAmount; }
    public void setAdmissionFeeAmount(BigDecimal admissionFeeAmount) { this.admissionFeeAmount = admissionFeeAmount; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public String getPenaltyType() { return penaltyType; }
    public void setPenaltyType(String penaltyType) { this.penaltyType = penaltyType; }

    public BigDecimal getFixedPenaltyAmount() { return fixedPenaltyAmount; }
    public void setFixedPenaltyAmount(BigDecimal fixedPenaltyAmount) { this.fixedPenaltyAmount = fixedPenaltyAmount; }

    public BigDecimal getPenaltyPercentage() { return penaltyPercentage; }
    public void setPenaltyPercentage(BigDecimal penaltyPercentage) { this.penaltyPercentage = penaltyPercentage; }

    public BigDecimal getMaxPenaltyCap() { return maxPenaltyCap; }
    public void setMaxPenaltyCap(BigDecimal maxPenaltyCap) { this.maxPenaltyCap = maxPenaltyCap; }

    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }

    public Boolean getGstIncludedInFee() { return gstIncludedInFee; }
    public void setGstIncludedInFee(Boolean gstIncludedInFee) { this.gstIncludedInFee = gstIncludedInFee; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}