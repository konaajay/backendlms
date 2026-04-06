package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_automation_control")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutomationControl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "low_stock_alert")
    private Boolean lowStockAlert;

    @Column(name = "min_threshold")
    private Integer minThreshold;

    @Column(name = "auto_reorder")
    private Boolean autoReorder;

    @Column(name = "reorder_quantity")
    private Integer reorderQuantity;

    @Column(name = "preferred_vendor_id")
    private Long preferredVendorId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "last_run_date")
    private LocalDateTime lastRunDate;

    @Column(name = "action_count")
    private Integer actionCount;

    // Aliases for service compatibility
    public Boolean getIsEnabled() { return isActive; }
    public void setIsEnabled(Boolean isEnabled) { this.isActive = isEnabled; }

    // Custom Builder to support isEnabled alias
    public static class AutomationControlBuilder {
        public AutomationControlBuilder isEnabled(Boolean isEnabled) {
            this.isActive = isEnabled;
            return this;
        }
    }
}