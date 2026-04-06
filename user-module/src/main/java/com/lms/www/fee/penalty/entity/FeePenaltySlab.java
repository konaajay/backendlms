package com.lms.www.fee.penalty.entity;

import com.lms.www.fee.structure.entity.FeeStructure;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fee_penalty_slabs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeePenaltySlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id")
    private FeeStructure feeStructure;

    @Column(name = "from_day", nullable = false)
    private Integer fromDay;

    @Column(name = "to_day", nullable = false)
    private Integer toDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_type", nullable = false, length = 20)
    private PenaltyType penaltyType;

    @Column(name = "penalty_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_schedule")
    private PaymentSchedule paymentSchedule;

    @Column(name = "period_count")
    private Integer periodCount;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean activeStatus = true;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    // Explicitly named getter and setter to avoid Lombok name collisions
    // and satisfy external calls to isActive() and setActive()
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
        this.activeStatus = active;
    }

    public enum PaymentSchedule {
        WEEKLY, MONTHLY, QUARTERLY, YEARLY, ONE_TIME
    }
}
