package com.lms.www.fee.discount.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "fee_discounts")
public class FeeDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("discountId")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fee_structure_id", nullable = false)
    private Long feeStructureId;

    @Column(name = "discount_name", nullable = false, length = 100)
    private String discountName;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason")
    private String reason;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_date")
    private LocalDate approvedDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public FeeDiscount() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum DiscountScope {
        STUDENT, BATCH, COURSE
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_scope", nullable = false)
    private DiscountScope discountScope;

    @Column(name = "scope_id", nullable = false)
    private Long scopeId;

    public static FeeDiscountBuilder builder() {
        return new FeeDiscountBuilder();
    }

    public static class FeeDiscountBuilder {
        private FeeDiscount discount = new FeeDiscount();
        public FeeDiscountBuilder userId(Long id) { discount.userId = id; return this; }
        public FeeDiscountBuilder feeStructureId(Long id) { discount.feeStructureId = id; return this; }
        public FeeDiscountBuilder discountName(String name) { discount.discountName = name; return this; }
        public FeeDiscountBuilder amount(BigDecimal amt) { discount.amount = amt; return this; }
        public FeeDiscountBuilder reason(String reason) { discount.reason = reason; return this; }
        public FeeDiscountBuilder discountScope(DiscountScope scope) { discount.discountScope = scope; return this; }
        public FeeDiscountBuilder scopeId(Long id) { discount.scopeId = id; return this; }
        public FeeDiscount build() { return discount; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getFeeStructureId() { return feeStructureId; }
    public void setFeeStructureId(Long id) { this.feeStructureId = id; }
    public String getDiscountName() { return discountName; }
    public void setDiscountName(String name) { this.discountName = name; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amt) { this.amount = amt; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long id) { this.approvedBy = id; }
    public LocalDate getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDate date) { this.approvedDate = date; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { this.isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }
    public DiscountScope getDiscountScope() { return discountScope; }
    public void setDiscountScope(DiscountScope scope) { this.discountScope = scope; }
    public Long getScopeId() { return scopeId; }
    public void setScopeId(Long id) { this.scopeId = id; }
}
