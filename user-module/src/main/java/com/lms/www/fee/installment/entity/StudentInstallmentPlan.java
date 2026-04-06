package com.lms.www.fee.installment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_installment_plans", uniqueConstraints = @UniqueConstraint(columnNames = {
        "student_fee_allocation_id",
        "installment_number" }))
public class StudentInstallmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long studentFeeAllocationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "installment_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InstallmentStatus status = InstallmentStatus.PENDING;

    @Column(name = "cashfree_order_id")
    private String cashfreeOrderId;

    @Column(name = "payment_session_id")
    private String paymentSessionId;

    @Column(name = "link_created_at")
    private LocalDateTime linkCreatedAt;

    @Column(name = "link_expiry")
    private LocalDateTime linkExpiry;

    @Column(name = "label")
    private String label;

    public enum InstallmentStatus {
        PENDING, PARTIAL, PAID, OVERDUE, LOCKED_FOR_EARLY_PAYMENT;

        public static InstallmentStatus from(String status) {
            try { return InstallmentStatus.valueOf(status.toUpperCase()); }
            catch (Exception e) { throw new IllegalArgumentException("Invalid installment status: " + status); }
        }
    }

    public static StudentInstallmentPlanBuilder builder() {
        return new StudentInstallmentPlanBuilder();
    }

    public static class StudentInstallmentPlanBuilder {
        private StudentInstallmentPlan plan = new StudentInstallmentPlan();
        public StudentInstallmentPlanBuilder studentFeeAllocationId(Long id) { plan.studentFeeAllocationId = id; return this; }
        public StudentInstallmentPlanBuilder installmentNumber(Integer num) { plan.installmentNumber = num; return this; }
        public StudentInstallmentPlanBuilder installmentAmount(BigDecimal amt) { plan.installmentAmount = amt; return this; }
        public StudentInstallmentPlanBuilder dueDate(LocalDate date) { plan.dueDate = date; return this; }
        public StudentInstallmentPlan build() { return plan; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentFeeAllocationId() { return studentFeeAllocationId; }
    public void setStudentFeeAllocationId(Long id) { this.studentFeeAllocationId = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long id) { this.userId = id; }
    public Integer getInstallmentNumber() { return installmentNumber; }
    public void setInstallmentNumber(Integer num) { this.installmentNumber = num; }
    public BigDecimal getInstallmentAmount() { return installmentAmount; }
    public void setInstallmentAmount(BigDecimal amt) { this.installmentAmount = amt; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate date) { this.dueDate = date; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal amt) { this.paidAmount = amt; }
    public InstallmentStatus getStatus() { return status; }
    public void setStatus(InstallmentStatus status) { this.status = status; }
    public String getCashfreeOrderId() { return cashfreeOrderId; }
    public void setCashfreeOrderId(String id) { this.cashfreeOrderId = id; }
    public String getPaymentSessionId() { return paymentSessionId; }
    public void setPaymentSessionId(String id) { this.paymentSessionId = id; }
    public LocalDateTime getLinkCreatedAt() { return linkCreatedAt; }
    public void setLinkCreatedAt(LocalDateTime dt) { this.linkCreatedAt = dt; }
    public LocalDateTime getLinkExpiry() { return linkExpiry; }
    public void setLinkExpiry(LocalDateTime dt) { this.linkExpiry = dt; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
