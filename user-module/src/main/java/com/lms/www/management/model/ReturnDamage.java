package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "returns_damage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReturnDamage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "issued_ref_id")
    private Long issuedRefId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "type", nullable = false)
    private String type; // RETURN / DAMAGE

    @Column(name = "item_condition")
    private String itemCondition;

    @Column(name = "action_required")
    private String actionRequired;

    @Column(name = "penalty_fee")
    private Double penaltyFee;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Builder.Default
    @Column(name = "status")
    private String status = "PENDING"; // PENDING / PROCESSED

    @Column(name = "returned_by")
    private Long returnedBy;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }

    // Aliases for compatibility
    public void setReportedBy(Long reportedBy) {
        this.returnedBy = reportedBy;
    }

    public Long getReportedBy() {
        return returnedBy;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.createdAt = reportedAt;
    }

    public LocalDateTime getReportedAt() {
        return createdAt;
    }
}