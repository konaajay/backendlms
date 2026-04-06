package com.lms.www.fee.penalty.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fee_penalties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeePenalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_installment_plan_id", nullable = false)
    private Long installmentId;

    @Column(name = "penalty_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "penalty_date", nullable = false)
    private LocalDate penaltyDate;

    @Column(length = 255)
    private String reason;

    @Column(name = "is_waived")
    @Builder.Default
    private boolean waived = false;

    @Column(name = "waived_by")
    private Long waivedBy;

    @Column(name = "waived_date")
    private LocalDateTime waivedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (penaltyDate == null) {
            penaltyDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
