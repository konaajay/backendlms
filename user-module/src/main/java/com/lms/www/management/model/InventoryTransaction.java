package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false, length = 255)
    private String itemId;

    @Column(name = "transaction_type", length = 50, nullable = false)
    private String transactionType; // INWARD / OUTWARD / RETURN / ADJUSTMENT / DAMAGE

    @Column(name = "quantity", nullable = false)
    private Integer quantity;


    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "performed_by")
    private String performedBy;

    @Column(name = "stock_type")
    private String stockType; // Added to match DB

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "before_stock")
    private Integer beforeStock;

    @Column(name = "after_stock")
    private Integer afterStock;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}