package com.lms.www.fee.structure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_types", indexes = {
        @Index(name = "idx_fee_type_name", columnList = "name", unique = true),
        @Index(name = "idx_fee_type_active", columnList = "is_active")
})
public class FeeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean mandatory = true;

    @Column(name = "is_refundable", nullable = false)
    private Boolean refundable = false;

    @Column(name = "is_one_time", nullable = false)
    private Boolean oneTime = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FeeType() {}

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static FeeTypeBuilder builder() {
        return new FeeTypeBuilder();
    }

    public static class FeeTypeBuilder {
        private FeeType entity = new FeeType();
        public FeeTypeBuilder name(String name) { entity.name = name; return this; }
        public FeeTypeBuilder description(String desc) { entity.description = desc; return this; }
        public FeeTypeBuilder active(Boolean active) { entity.active = active; return this; }
        public FeeTypeBuilder mandatory(Boolean mandatory) { entity.mandatory = mandatory; return this; }
        public FeeTypeBuilder refundable(Boolean refundable) { entity.refundable = refundable; return this; }
        public FeeTypeBuilder oneTime(Boolean oneTime) { entity.oneTime = oneTime; return this; }
        public FeeTypeBuilder displayOrder(Integer order) { entity.displayOrder = order; return this; }
        public FeeType build() { return entity; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Boolean getMandatory() { return mandatory; }
    public void setMandatory(Boolean mandatory) { this.mandatory = mandatory; }
    public Boolean getRefundable() { return refundable; }
    public void setRefundable(Boolean refundable) { this.refundable = refundable; }
    public Boolean getOneTime() { return oneTime; }
    public void setOneTime(Boolean oneTime) { this.oneTime = oneTime; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer order) { this.displayOrder = order; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }
}
