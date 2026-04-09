package com.lms.www.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "digital_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DigitalAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("name")
    @Column(name = "software_name")
    private String softwareName;

    @Column(name = "license_key", unique = true)
    private String licenseKey;

    @Column(name = "total_licenses")
    private Integer totalLicenses;

    @Column(name = "used_licenses")
    private Integer usedLicenses;

    @Column(name = "available_licenses")
    private Integer availableLicenses;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column(name = "vendor_id")
    private Long vendorId; // ✅ NEW

    @Column(name = "cost_per_license")
    private Double costPerLicense; // ✅ NEW

    @Column(name = "activation_date")
    private LocalDate activationDate;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "status")
    private String status;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks; // ✅ NEW

    @Column(name = "auto_reclaim")
    private Boolean autoReclaim; // ✅ NEW

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // ✅ NEW

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