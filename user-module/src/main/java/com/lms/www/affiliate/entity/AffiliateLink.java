package com.lms.www.affiliate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliate_links", uniqueConstraints = {
        @UniqueConstraint(name = "uk_affiliate_batch", columnNames = { "affiliate_id", "batch_id" })
}, indexes = {
        @Index(name = "idx_aff_link_affiliate", columnList = "affiliate_id"),
        @Index(name = "idx_aff_link_batch", columnList = "batch_id"),
        @Index(name = "idx_aff_link_referral_code", columnList = "referral_code"),
        @Index(name = "idx_affiliate_batch_lookup", columnList = "affiliate_id, batch_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliateLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false, foreignKey = @ForeignKey(name = "fk_affiliate_link_affiliate"))
    private Affiliate affiliate;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "referral_code", nullable = false, unique = true, length = 20)
    private String referralCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LinkStatus status = LinkStatus.ACTIVE;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "commission_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal commissionValue;

    @Column(name = "student_discount_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal studentDiscountValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Affiliate getAffiliate() { return affiliate; }
    public void setAffiliate(Affiliate affiliate) { this.affiliate = affiliate; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }
    public LinkStatus getStatus() { return status; }
    public void setStatus(LinkStatus status) { this.status = status; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public BigDecimal getCommissionValue() { return commissionValue; }
    public void setCommissionValue(BigDecimal commissionValue) { this.commissionValue = commissionValue; }
    public BigDecimal getStudentDiscountValue() { return studentDiscountValue; }
    public void setStudentDiscountValue(BigDecimal studentDiscountValue) { this.studentDiscountValue = studentDiscountValue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum LinkStatus {
        ACTIVE, INACTIVE
    }
}