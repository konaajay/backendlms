package com.lms.www.fee.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee_refunds")
public class StudentFeeRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long allocationId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_type", nullable = true)
    private RefundType refundType; // FULL, PARTIAL

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_mode", nullable = true)
    private RefundMode refundMode; // BANK, WALLET, CASH

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private RefundStatus status = RefundStatus.PENDING;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    public enum RefundType {
        FULL, PARTIAL
    }

    public enum RefundMode {
        BANK, WALLET, CASH
    }

    public enum RefundStatus {
        PENDING, APPROVED, REJECTED
    }

    public StudentFeeRefund() {}

    @PrePersist
    void onCreate() {
        requestDate = LocalDateTime.now();
        if (status == null)
            status = RefundStatus.PENDING;
    }

    // Manual Builder
    public static StudentFeeRefundBuilder builder() {
        return new StudentFeeRefundBuilder();
    }

    public static class StudentFeeRefundBuilder {
        private Long allocationId;
        private Long userId;
        private BigDecimal amount;
        private String reason;
        private String status = "PENDING";

        public StudentFeeRefundBuilder allocationId(Long id) { this.allocationId = id; return this; }
        public StudentFeeRefundBuilder userId(Long id) { this.userId = id; return this; }
        public StudentFeeRefundBuilder amount(BigDecimal amt) { this.amount = amt; return this; }
        public StudentFeeRefundBuilder reason(String reason) { this.reason = reason; return this; }
        public StudentFeeRefundBuilder status(String status) { this.status = status; return this; }

        public StudentFeeRefund build() {
            StudentFeeRefund refund = new StudentFeeRefund();
            refund.setAllocationId(this.allocationId);
            refund.setUserId(this.userId);
            refund.setAmount(this.amount);
            refund.setRefundReason(this.reason);
            if (this.status != null) {
                refund.setStatus(RefundStatus.valueOf(this.status.toUpperCase()));
            }
            return refund;
        }
    }

    // Manual Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAllocationId() { return allocationId; }
    public void setAllocationId(Long id) { this.allocationId = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amt) { this.amount = amt; }

    public RefundType getRefundType() { return refundType; }
    public void setRefundType(RefundType type) { this.refundType = type; }

    public RefundMode getRefundMode() { return refundMode; }
    public void setRefundMode(RefundMode mode) { this.refundMode = mode; }

    public RefundStatus getStatus() { return status; }
    public void setStatus(RefundStatus status) { this.status = status; }

    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String reason) { this.refundReason = reason; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String reason) { this.rejectionReason = reason; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long id) { this.approvedBy = id; }

    public Long getRejectedBy() { return rejectedBy; }
    public void setRejectedBy(Long id) { this.rejectedBy = id; }

    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime dt) { this.requestDate = dt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime dt) { this.approvedAt = dt; }

    public LocalDateTime getProcessedDate() { return processedDate; }
    public void setProcessedDate(LocalDateTime dt) { this.processedDate = dt; }
}
