package com.lms.www.affiliate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliate_sales", indexes = {
        @Index(name = "idx_sale_affiliate", columnList = "affiliate_id"),
        @Index(name = "idx_batch_sale", columnList = "batch_id"),
        @Index(name = "idx_order_id", columnList = "order_id"),
        @Index(name = "idx_sale_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliateSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private Affiliate affiliate;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "lead_id")
    private Long leadId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "original_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal originalAmount;

    @Column(name = "discount_amount", precision = 19, scale = 4)
    private BigDecimal discountAmount;

    @Column(name = "order_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal orderAmount;

    @Column(name = "commission_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal commissionAmount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SaleStatus status;

    // payout reference
    @Column(name = "payout_id")
    private Long payoutId;

    // timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public enum SaleStatus {
        PENDING,
        APPROVED,
        CREDITED,
        PAID,
        CANCELLED
    }

    // Manual Setters
    public void setAffiliate(Affiliate affiliate) { this.affiliate = affiliate; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }
    public void setStatus(SaleStatus status) { this.status = status; }
    public void setPayoutId(Long payoutId) { this.payoutId = payoutId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    // Manual Getters
    public Affiliate getAffiliate() { return affiliate; }
    public Long getCourseId() { return courseId; }
    public Long getBatchId() { return batchId; }
    public String getOrderId() { return orderId; }
    public Long getLeadId() { return leadId; }
    public Long getStudentId() { return studentId; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getOrderAmount() { return orderAmount; }
    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public SaleStatus getStatus() { return status; }
    public Long getPayoutId() { return payoutId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public LocalDateTime getPaidAt() { return paidAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = SaleStatus.PENDING;
        }

        if (originalAmount == null) originalAmount = BigDecimal.ZERO;
        if (orderAmount == null) orderAmount = BigDecimal.ZERO;
        if (commissionAmount == null) commissionAmount = BigDecimal.ZERO;
    }

    // Explicit builder because Lombok is failing in this environment
    public static AffiliateSaleBuilder builder() {
        return new AffiliateSaleBuilder();
    }

    public static class AffiliateSaleBuilder {
        private AffiliateSale sale = new AffiliateSale();

        public AffiliateSaleBuilder affiliate(Affiliate affiliate) { sale.setAffiliate(affiliate); return this; }
        public AffiliateSaleBuilder leadId(Long leadId) { sale.setLeadId(leadId); return this; }
        public AffiliateSaleBuilder studentId(Long studentId) { sale.setStudentId(studentId); return this; }
        public AffiliateSaleBuilder courseId(Long courseId) { sale.setCourseId(courseId); return this; }
        public AffiliateSaleBuilder batchId(Long batchId) { sale.setBatchId(batchId); return this; }
        public AffiliateSaleBuilder orderId(String orderId) { sale.setOrderId(orderId); return this; }
        public AffiliateSaleBuilder originalAmount(BigDecimal amount) { sale.setOriginalAmount(amount); return this; }
        public AffiliateSaleBuilder orderAmount(BigDecimal amount) { sale.setOrderAmount(amount); return this; }
        public AffiliateSaleBuilder commissionAmount(BigDecimal amount) { sale.setCommissionAmount(amount); return this; }
        public AffiliateSaleBuilder discountAmount(BigDecimal amount) { sale.setDiscountAmount(amount); return this; }
        public AffiliateSaleBuilder status(SaleStatus status) { sale.setStatus(status); return this; }
        public AffiliateSale build() { return sale; }
    }
}