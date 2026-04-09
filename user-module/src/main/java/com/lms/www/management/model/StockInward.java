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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "stock_inward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockInward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "po_reference")
    private String poReference;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @JsonProperty("itemId")
    @Column(name = "item_id", length = 255)
    private String itemId;

    @JsonProperty("quantity")
    @Column(name = "quantity_received")
    private Integer quantityReceived;

    @Column(name = "cost_per_unit")
    private Double costPerUnit;

    @JsonProperty("gstTax")
    @Column(name = "tax_percent")
    private Double taxPercent;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "invoice_file")
    private String invoiceFile;

    @Column(name = "received_by")
    private String receivedBy;

    @JsonProperty("date")
    @Column(name = "received_date")
    private java.time.LocalDate receivedDate; // Changed to LocalDate for frontend date strings

    @Column(name = "status")
    private String status;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null)
            this.status = "RECEIVED";
    }

    // Helper Setters for Frontend IDs
    @JsonProperty("vendorId")
    public void setVendorId(Long vendorId) {
        if (vendorId != null) {
            this.vendor = Vendor.builder().id(vendorId).build();
        }
    }

    @JsonProperty("itemId")
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}