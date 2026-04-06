package com.lms.www.fee.admin.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund_rules")
public class RefundRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ruleName;

    @Column(name = "days_before_start")
    private Integer daysBeforeStart;

    @Column(name = "refund_percentage", precision = 5, scale = 2)
    private BigDecimal refundPercentage;

    @Column(name = "is_active")
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RefundRule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public Integer getDaysBeforeStart() { return daysBeforeStart; }
    public void setDaysBeforeStart(Integer daysBeforeStart) { this.daysBeforeStart = daysBeforeStart; }
    public BigDecimal getRefundPercentage() { return refundPercentage; }
    public void setRefundPercentage(BigDecimal refundPercentage) { this.refundPercentage = refundPercentage; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }

    public static RefundRuleBuilder builder() { return new RefundRuleBuilder(); }
    public static class RefundRuleBuilder {
        private RefundRule instance = new RefundRule();
        public RefundRuleBuilder id(Long id) { instance.setId(id); return this; }
        public RefundRuleBuilder ruleName(String name) { instance.setRuleName(name); return this; }
        public RefundRuleBuilder daysBeforeStart(Integer days) { instance.setDaysBeforeStart(days); return this; }
        public RefundRuleBuilder refundPercentage(BigDecimal pct) { instance.setRefundPercentage(pct); return this; }
        public RefundRuleBuilder active(Boolean active) { instance.setActive(active); return this; }
        public RefundRule build() { return instance; }
    }
}
