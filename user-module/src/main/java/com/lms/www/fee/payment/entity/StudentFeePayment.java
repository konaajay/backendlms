package com.lms.www.fee.payment.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "student_fee_payments", indexes = {
        @Index(name = "idx_cashfree_order_id", columnList = "cashfree_order_id"),
        @Index(name = "idx_transaction_ref", columnList = "transaction_reference"),
        @Index(name = "idx_allocation_id", columnList = "student_fee_allocation_id")
})
public class StudentFeePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("paymentId")
    private Long id;

    @Version
    private Long version;

    @JsonProperty("allocationId")
    @Column(name = "student_fee_allocation_id", nullable = false)
    private Long studentFeeAllocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_allocation_id", insertable = false, updatable = false)
    private StudentFeeAllocation allocation;

    @JsonProperty("installmentId")
    @Column(name = "student_installment_plan_id")
    private Long studentInstallmentPlanId;

    @JsonProperty("paidAmount")
    @DecimalMin(value = "0.01", message = "Paid amount must be greater than zero")
    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @JsonProperty("paymentDate")
    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "transaction_reference", nullable = false, unique = true)
    private String transactionReference;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "screenshot_url")
    private String screenshotUrl;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "recorded_by")
    private Long recordedBy;

    // --- Bulk Payment Fields ---
    @Column(name = "installment_total", precision = 12, scale = 2)
    private BigDecimal installmentTotal;

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "penalty_amount", precision = 12, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "overdue_remaining", precision = 12, scale = 2)
    private BigDecimal overdueRemaining;

    @ManyToMany
    @JoinTable(
        name = "payment_installment_mapping",
        joinColumns = @JoinColumn(name = "payment_id"),
        inverseJoinColumns = @JoinColumn(name = "installment_id")
    )
    @Builder.Default
    private List<StudentInstallmentPlan> installments = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String remarks;

    // --- Integration Fields (Cashfree / Webhooks ) ---
    @Column(name = "cashfree_order_id", nullable = false, unique = true)
    private String cashfreeOrderId;

    @Column(name = "payment_session_id")
    private String paymentSessionId;

    @Column(name = "gateway_payment_status")
    private String gatewayPaymentStatus;

    @Column(name = "signature_verified", nullable = false)
    @Builder.Default
    private boolean signatureVerified = false;

    @Column(name = "raw_gateway_response", columnDefinition = "TEXT")
    private String rawGatewayResponse;

    @Column(name = "gateway_amount", precision = 12, scale = 2)
    private BigDecimal gatewayAmount;

    @Column(name = "gateway_payment_time")
    private LocalDateTime gatewayPaymentTime;

/*
    @Column(name = "is_early_payment", nullable = false)
    @Builder.Default
    private boolean earlyPayment = false;
*/

/*
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
*/



    @PrePersist
    protected void onPrePersist() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (paymentStatus == null) {
            paymentStatus = PaymentStatus.PENDING;
        }
    }
}
