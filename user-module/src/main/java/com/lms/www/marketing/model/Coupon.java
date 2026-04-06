package com.lms.www.marketing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "coupons", indexes = {
        @Index(name = "idx_coupon_code", columnList = "code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType; // PERCENT, FIXED

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "discount_cap")
    private Double discountCap; // Maximum ₹ off for PERCENT types

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "max_usage")
    private Integer maxUsage;

    @Column(name = "used_count")
    private int usedCount = 0;

    @Column(name = "min_purchase_amount")
    private double minPurchaseAmount = 0.0;

    @Column(name = "per_user_limit")
    private int perUserLimit = 1;

    @Column(name = "is_first_order_only")
    private boolean isFirstOrderOnly = false;

    @Column(name = "auto_apply")
    private boolean autoApply = false;

    @Column(name = "affiliate_id")
    private Long affiliateId;

    @Column(name = "learner_id")
    private Long learnerId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CouponStatus status = CouponStatus.ACTIVE;

    private boolean deleted = false;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponCourse> couponCourses;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
