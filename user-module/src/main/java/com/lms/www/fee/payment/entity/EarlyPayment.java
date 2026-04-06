package com.lms.www.fee.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "early_payments")
public class EarlyPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "early_payment_installments", joinColumns = @JoinColumn(name = "early_payment_id"))
    @Column(name = "installment_id")
    private List<Long> installmentIds;

    @Column(name = "total_original_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalOriginalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "final_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "cashfree_order_id")
    private String cashfreeOrderId;

    @Column(name = "payment_session_id")
    private String paymentSessionId;

    @Column(name = "link_created_at")
    private LocalDateTime linkCreatedAt;

    @Column(name = "link_expiry")
    private LocalDateTime linkExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EarlyPaymentStatus status = EarlyPaymentStatus.CREATED;

    public EarlyPayment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public List<Long> getInstallmentIds() { return installmentIds; }
    public void setInstallmentIds(List<Long> installmentIds) { this.installmentIds = installmentIds; }

    public BigDecimal getTotalOriginalAmount() { return totalOriginalAmount; }
    public void setTotalOriginalAmount(BigDecimal totalOriginalAmount) { this.totalOriginalAmount = totalOriginalAmount; }

    public DiscountType getDiscountType() { return discountType; }
    public void setDiscountType(DiscountType discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getCashfreeOrderId() { return cashfreeOrderId; }
    public void setCashfreeOrderId(String cashfreeOrderId) { this.cashfreeOrderId = cashfreeOrderId; }

    public String getPaymentSessionId() { return paymentSessionId; }
    public void setPaymentSessionId(String paymentSessionId) { this.paymentSessionId = paymentSessionId; }

    public LocalDateTime getLinkCreatedAt() { return linkCreatedAt; }
    public void setLinkCreatedAt(LocalDateTime linkCreatedAt) { this.linkCreatedAt = linkCreatedAt; }

    public LocalDateTime getLinkExpiry() { return linkExpiry; }
    public void setLinkExpiry(LocalDateTime linkExpiry) { this.linkExpiry = linkExpiry; }

    public EarlyPaymentStatus getStatus() { return status; }
    public void setStatus(EarlyPaymentStatus status) { this.status = status; }

    public enum EarlyPaymentStatus {
        CREATED, PAID, FAILED, EXPIRED;

        public static EarlyPaymentStatus from(String status) {
            try {
                return EarlyPaymentStatus.valueOf(status.toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid early payment status: " + status);
            }
        }
    }

    public enum DiscountType {
        FLAT, PERCENT;

        public static DiscountType from(String type) {
            try {
                return DiscountType.valueOf(type.toUpperCase());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid discount type: " + type);
            }
        }
    }

    public static EarlyPaymentBuilder builder() {
        return new EarlyPaymentBuilder();
    }

    public static class EarlyPaymentBuilder {
        private EarlyPayment instance = new EarlyPayment();

        public EarlyPaymentBuilder id(Long id) { instance.setId(id); return this; }
        public EarlyPaymentBuilder studentId(Long id) { instance.setStudentId(id); return this; }
        public EarlyPaymentBuilder installmentIds(List<Long> ids) { instance.setInstallmentIds(ids); return this; }
        public EarlyPaymentBuilder totalOriginalAmount(BigDecimal amount) { instance.setTotalOriginalAmount(amount); return this; }
        public EarlyPaymentBuilder discountType(DiscountType type) { instance.setDiscountType(type); return this; }
        public EarlyPaymentBuilder discountValue(BigDecimal value) { instance.setDiscountValue(value); return this; }
        public EarlyPaymentBuilder finalAmount(BigDecimal amount) { instance.setFinalAmount(amount); return this; }
        public EarlyPaymentBuilder cashfreeOrderId(String id) { instance.setCashfreeOrderId(id); return this; }
        public EarlyPaymentBuilder paymentSessionId(String id) { instance.setPaymentSessionId(id); return this; }
        public EarlyPaymentBuilder linkCreatedAt(LocalDateTime dt) { instance.setLinkCreatedAt(dt); return this; }
        public EarlyPaymentBuilder linkExpiry(LocalDateTime dt) { instance.setLinkExpiry(dt); return this; }
        public EarlyPaymentBuilder status(EarlyPaymentStatus status) { instance.setStatus(status); return this; }
        public EarlyPayment build() { return instance; }
    }
}
