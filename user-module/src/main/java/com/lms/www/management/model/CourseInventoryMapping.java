package com.lms.www.management.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "course_inventory_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseInventoryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "item_id", length = 255)
    private String itemId;

    @Column(name = "mandatory")
    private Boolean mandatory;

    @Column(name = "auto_reserve")
    private Boolean autoReserve;

    private Double price;

    private Boolean refundable;

    @Column(name = "quantity_required")
    private Integer quantityRequired;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Transient fields for the UI
    @Transient
    private String name; // This corresponds to Course Name in UI

    @Transient
    @Builder.Default
    private java.util.List<String> mandatoryItems = new java.util.ArrayList<>();

    @Transient
    @Builder.Default
    private java.util.List<String> optionalItems = new java.util.ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonProperty("itemId")
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("itemId")
    public String getItemId() {
        return itemId;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = "ACTIVE";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}