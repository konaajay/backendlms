package com.lms.www.affiliate.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "commission_rules",
    indexes = {
        @Index(name = "idx_course_rule", columnList = "course_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "affiliate_percent", nullable = false, precision = 19, scale = 4)
    private BigDecimal affiliatePercent;

    @Column(name = "student_discount_percent", nullable = false, precision = 19, scale = 4)
    private BigDecimal studentDiscountPercent;

    @Column(name = "is_bonus", nullable = false)
    private boolean isBonus;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (affiliatePercent == null) affiliatePercent = BigDecimal.ZERO;
        if (studentDiscountPercent == null) studentDiscountPercent = BigDecimal.ZERO;
    }
}