package com.lms.www.fee.structure.entity;

import com.lms.www.fee.penalty.entity.FeePenaltySlab;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fee_structures")
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "structure_name", nullable = false)
    private String name;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = true)
    private Long batchId;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "base_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "fee_type_id")
    private Long feeTypeId;

    @Column(name = "batch_name")
    private String batchName;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "currency")
    private String currency = "INR";

    @Column(name = "admission_fee_amount", precision = 12, scale = 2)
    private BigDecimal admissionFeeAmount = BigDecimal.ZERO;

    @Column(name = "admission_non_refundable", nullable = false)
    private Boolean admissionNonRefundable = false;

    @Column(name = "gst_included_in_fee", nullable = false)
    private Boolean gstIncludedInFee = false;

    // Installment Config
    @Column(name = "installment_count")
    private Integer installmentCount;

    @NotNull(message = "Duration in months is required")
    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "grace_days")
    private Integer graceDays = 5;

    // GST
    @Column(name = "gst_applicable", nullable = false)
    private Boolean gstApplicable = true;

    @Column(name = "gst_percent", precision = 5, scale = 2)
    private BigDecimal gstPercent = BigDecimal.valueOf(18);

    // Penalty Rules (New Framework)
    @Column(name = "penalty_type", length = 20)
    private String penaltyType = "NONE"; 

    @Column(name = "max_penalty_cap", precision = 12, scale = 2)
    private BigDecimal maxPenaltyCap = BigDecimal.ZERO;

    @Column(name = "penalty_percentage", precision = 5, scale = 2)
    private BigDecimal penaltyPercentage = BigDecimal.ZERO;

    @Column(name = "fixed_penalty_amount", precision = 12, scale = 2)
    private BigDecimal fixedPenaltyAmount = BigDecimal.ZERO;

    // Batch Discount 
    @Column(name = "discount_type", length = 20)
    private String discountType = "NONE"; 

    @Column(name = "discount_value", precision = 12, scale = 2)
    private BigDecimal discountValue = BigDecimal.ZERO;

    // Components
    @OneToMany(mappedBy = "feeStructure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeeStructureComponent> components = new ArrayList<>();

    // Penalty Slabs
    @OneToMany(mappedBy = "feeStructure", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeePenaltySlab> slabs = new ArrayList<>();

    @Transient
    private List<FeeStructureComponent> additionalFeeComponents;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public FeeStructure() {}

    public void addComponent(FeeStructureComponent component) {
        if (components == null) components = new ArrayList<>();
        components.add(component);
        component.setFeeStructure(this);
    }

    public void addSlab(FeePenaltySlab slab) {
        if (slabs == null) slabs = new ArrayList<>();
        slabs.add(slab);
        slab.setFeeStructure(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }
    public Boolean isActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Long getFeeTypeId() { return feeTypeId; }
    public void setFeeTypeId(Long feeTypeId) { this.feeTypeId = feeTypeId; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getAdmissionFeeAmount() { return admissionFeeAmount; }
    public void setAdmissionFeeAmount(BigDecimal admissionFeeAmount) { this.admissionFeeAmount = admissionFeeAmount; }
    public Boolean getAdmissionNonRefundable() { return admissionNonRefundable; }
    public void setAdmissionNonRefundable(Boolean admissionNonRefundable) { this.admissionNonRefundable = admissionNonRefundable; }
    public Boolean getGstIncludedInFee() { return gstIncludedInFee; }
    public void setGstIncludedInFee(Boolean gstIncludedInFee) { this.gstIncludedInFee = gstIncludedInFee; }
    public Integer getInstallmentCount() { return installmentCount; }
    public void setInstallmentCount(Integer installmentCount) { this.installmentCount = installmentCount; }
    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getGraceDays() { return graceDays; }
    public void setGraceDays(Integer graceDays) { this.graceDays = graceDays; }
    public Boolean getGstApplicable() { return gstApplicable; }
    public void setGstApplicable(Boolean gstApplicable) { this.gstApplicable = gstApplicable; }
    public BigDecimal getGstPercent() { return gstPercent; }
    public void setGstPercent(BigDecimal gstPercent) { this.gstPercent = gstPercent; }
    public String getPenaltyType() { return penaltyType; }
    public void setPenaltyType(String penaltyType) { this.penaltyType = penaltyType; }
    public BigDecimal getMaxPenaltyCap() { return maxPenaltyCap; }
    public void setMaxPenaltyCap(BigDecimal maxPenaltyCap) { this.maxPenaltyCap = maxPenaltyCap; }
    public BigDecimal getPenaltyPercentage() { return penaltyPercentage; }
    public void setPenaltyPercentage(BigDecimal penaltyPercentage) { this.penaltyPercentage = penaltyPercentage; }
    public BigDecimal getFixedPenaltyAmount() { return fixedPenaltyAmount; }
    public void setFixedPenaltyAmount(BigDecimal fixedPenaltyAmount) { this.fixedPenaltyAmount = fixedPenaltyAmount; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public List<FeeStructureComponent> getComponents() { return components; }
    public void setComponents(List<FeeStructureComponent> components) { this.components = components; }
    public List<FeePenaltySlab> getSlabs() { return slabs; }
    public void setSlabs(List<FeePenaltySlab> slabs) { this.slabs = slabs; }
    public List<FeeStructureComponent> getAdditionalFeeComponents() { return additionalFeeComponents; }
    public void setAdditionalFeeComponents(List<FeeStructureComponent> additionalFeeComponents) { this.additionalFeeComponents = additionalFeeComponents; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
