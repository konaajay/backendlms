package com.lms.www.fee.allocation.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_allocations", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "batch_id" }))
public class StudentFeeAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fee_structure_id", nullable = false)
    private Long feeStructureId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_email")
    private String studentEmail;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "batch_name")
    private String batchName;

    @Column(name = "original_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalAmount;

    @Column(name = "admin_discount", precision = 12, scale = 2)
    private BigDecimal adminDiscount = BigDecimal.ZERO;

    @Column(name = "additional_discount", precision = 12, scale = 2)
    private BigDecimal additionalDiscount = BigDecimal.ZERO;

    @Column(name = "total_discount", precision = 12, scale = 2)
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(name = "one_time_amount", precision = 12, scale = 2)
    private BigDecimal oneTimeAmount = BigDecimal.ZERO;

    @Column(name = "installment_amount", precision = 12, scale = 2)
    private BigDecimal installmentAmount = BigDecimal.ZERO;

    @Column(name = "admission_fee_amount", precision = 12, scale = 2)
    private BigDecimal admissionFeeAmount = BigDecimal.ZERO;

    @Column(name = "gst_rate", precision = 5, scale = 2)
    private BigDecimal gstRate = BigDecimal.ZERO;

    @Column(name = "gst_amount", precision = 12, scale = 2)
    private BigDecimal gstAmount = BigDecimal.ZERO;

    @Column(name = "payable_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal payableAmount;

    @Column(name = "advance_payment", precision = 12, scale = 2)
    private BigDecimal advancePayment = BigDecimal.ZERO;

    @Column(name = "remaining_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "currency", length = 10)
    private String currency = "INR";

    @Column(name = "applied_promo_code")
    private String appliedPromoCode;

    @Column(name = "promo_discount", precision = 12, scale = 2)
    private BigDecimal promoDiscount = BigDecimal.ZERO;

    @Column(name = "affiliate_discount", precision = 12, scale = 2)
    private BigDecimal affiliateDiscount = BigDecimal.ZERO;

    @Column(name = "affiliate_id")
    private Long affiliateId;

    @Column(name = "installment_count", nullable = false)
    private Integer installmentCount;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AllocationStatus status = AllocationStatus.ACTIVE;

    public enum AllocationStatus {
        ACTIVE, COMPLETED, CANCELLED, REFUNDED;

        public static AllocationStatus from(String status) {
            try { return AllocationStatus.valueOf(status.toUpperCase()); }
            catch (Exception e) { throw new IllegalArgumentException("Invalid allocation status: " + status); }
        }
    }

    public StudentFeeAllocation() {}

    public BigDecimal getPaidAmount() {
        return (payableAmount != null && remainingAmount != null) ? payableAmount.subtract(remainingAmount) : BigDecimal.ZERO;
    }

    @Column(name = "allocation_date")
    private LocalDate allocationDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        if (allocationDate == null) { allocationDate = LocalDate.now(); }
    }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

    public static StudentFeeAllocationBuilder builder() { return new StudentFeeAllocationBuilder(); }
    public static class StudentFeeAllocationBuilder {
        private StudentFeeAllocation a = new StudentFeeAllocation();
        public StudentFeeAllocationBuilder userId(Long id) { a.userId = id; return this; }
        public StudentFeeAllocationBuilder feeStructureId(Long id) { a.feeStructureId = id; return this; }
        public StudentFeeAllocationBuilder batchId(Long id) { a.batchId = id; return this; }
        public StudentFeeAllocationBuilder courseId(Long id) { a.courseId = id; return this; }
        public StudentFeeAllocationBuilder studentName(String name) { a.studentName = name; return this; }
        public StudentFeeAllocationBuilder studentEmail(String email) { a.studentEmail = email; return this; }
        public StudentFeeAllocationBuilder courseName(String name) { a.courseName = name; return this; }
        public StudentFeeAllocationBuilder batchName(String name) { a.batchName = name; return this; }
        public StudentFeeAllocationBuilder originalAmount(BigDecimal amt) { a.originalAmount = amt; return this; }
        public StudentFeeAllocationBuilder adminDiscount(BigDecimal amt) { a.adminDiscount = amt; return this; }
        public StudentFeeAllocationBuilder additionalDiscount(BigDecimal amt) { a.additionalDiscount = amt; return this; }
        public StudentFeeAllocationBuilder totalDiscount(BigDecimal amt) { a.totalDiscount = amt; return this; }
        public StudentFeeAllocationBuilder oneTimeAmount(BigDecimal amt) { a.oneTimeAmount = amt; return this; }
        public StudentFeeAllocationBuilder installmentAmount(BigDecimal amt) { a.installmentAmount = amt; return this; }
        public StudentFeeAllocationBuilder admissionFeeAmount(BigDecimal amt) { a.admissionFeeAmount = amt; return this; }
        public StudentFeeAllocationBuilder gstRate(BigDecimal rate) { a.gstRate = rate; return this; }
        public StudentFeeAllocationBuilder gstAmount(BigDecimal amt) { a.gstAmount = amt; return this; }
        public StudentFeeAllocationBuilder payableAmount(BigDecimal amt) { a.payableAmount = amt; return this; }
        public StudentFeeAllocationBuilder advancePayment(BigDecimal amt) { a.advancePayment = amt; return this; }
        public StudentFeeAllocationBuilder remainingAmount(BigDecimal amt) { a.remainingAmount = amt; return this; }
        public StudentFeeAllocationBuilder currency(String cur) { a.currency = cur; return this; }
        public StudentFeeAllocationBuilder appliedPromoCode(String code) { a.appliedPromoCode = code; return this; }
        public StudentFeeAllocationBuilder promoDiscount(BigDecimal amt) { a.promoDiscount = amt; return this; }
        public StudentFeeAllocationBuilder affiliateDiscount(BigDecimal amt) { a.affiliateDiscount = amt; return this; }
        public StudentFeeAllocationBuilder affiliateId(Long id) { a.affiliateId = id; return this; }
        public StudentFeeAllocationBuilder installmentCount(Integer count) { a.installmentCount = count; return this; }
        public StudentFeeAllocationBuilder durationMonths(Integer months) { a.durationMonths = months; return this; }
        public StudentFeeAllocationBuilder status(AllocationStatus status) { a.status = status; return this; }
        public StudentFeeAllocationBuilder allocationDate(LocalDate date) { a.allocationDate = date; return this; }
        public StudentFeeAllocation build() { return a; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getFeeStructureId() { return feeStructureId; }
    public void setFeeStructureId(Long id) { this.feeStructureId = id; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long id) { this.batchId = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long id) { this.courseId = id; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String name) { this.studentName = name; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String email) { this.studentEmail = email; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String name) { this.courseName = name; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String name) { this.batchName = name; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal amt) { this.originalAmount = amt; }
    public BigDecimal getAdminDiscount() { return adminDiscount; }
    public void setAdminDiscount(BigDecimal amt) { this.adminDiscount = amt; }
    public BigDecimal getAdditionalDiscount() { return additionalDiscount; }
    public void setAdditionalDiscount(BigDecimal amt) { this.additionalDiscount = amt; }
    public BigDecimal getTotalDiscount() { return totalDiscount; }
    public void setTotalDiscount(BigDecimal amt) { this.totalDiscount = amt; }
    public BigDecimal getOneTimeAmount() { return oneTimeAmount; }
    public void setOneTimeAmount(BigDecimal amt) { this.oneTimeAmount = amt; }
    public BigDecimal getInstallmentAmount() { return installmentAmount; }
    public void setInstallmentAmount(BigDecimal amt) { this.installmentAmount = amt; }
    public BigDecimal getAdmissionFeeAmount() { return admissionFeeAmount; }
    public void setAdmissionFeeAmount(BigDecimal amt) { this.admissionFeeAmount = amt; }
    public BigDecimal getGstRate() { return gstRate; }
    public void setGstRate(BigDecimal rate) { this.gstRate = rate; }
    public BigDecimal getGstAmount() { return gstAmount; }
    public void setGstAmount(BigDecimal amt) { this.gstAmount = amt; }
    public BigDecimal getPayableAmount() { return payableAmount; }
    public void setPayableAmount(BigDecimal amt) { this.payableAmount = amt; }
    public BigDecimal getAdvancePayment() { return advancePayment; }
    public void setAdvancePayment(BigDecimal amt) { this.advancePayment = amt; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal amt) { this.remainingAmount = amt; }
    public String getCurrency() { return currency; }
    public void setCurrency(String cur) { this.currency = cur; }
    public String getAppliedPromoCode() { return appliedPromoCode; }
    public void setAppliedPromoCode(String code) { this.appliedPromoCode = code; }
    public BigDecimal getPromoDiscount() { return promoDiscount; }
    public void setPromoDiscount(BigDecimal amt) { this.promoDiscount = amt; }
    public BigDecimal getAffiliateDiscount() { return affiliateDiscount; }
    public void setAffiliateDiscount(BigDecimal amt) { this.affiliateDiscount = amt; }
    public Long getAffiliateId() { return affiliateId; }
    public void setAffiliateId(Long id) { this.affiliateId = id; }
    public Integer getInstallmentCount() { return installmentCount; }
    public void setInstallmentCount(Integer count) { this.installmentCount = count; }
    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer months) { this.durationMonths = months; }
    public AllocationStatus getStatus() { return status; }
    public void setStatus(AllocationStatus status) { this.status = status; }
    public LocalDate getAllocationDate() { return allocationDate; }
    public void setAllocationDate(LocalDate date) { this.allocationDate = date; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }
}
