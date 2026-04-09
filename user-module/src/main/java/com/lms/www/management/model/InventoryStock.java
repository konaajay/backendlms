package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false, unique = true, length = 255)
    private String itemId;

    @Column(name = "total_stock")
    private Integer totalStock;

    @Column(name = "available_stock")
    private Integer availableStock;

    @Column(name = "reserved_stock")
    private Integer reservedStock;

    @Column(name = "damaged_stock")
    private Integer damagedStock;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}