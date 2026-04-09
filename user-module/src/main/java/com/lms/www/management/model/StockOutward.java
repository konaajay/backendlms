package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "stock_outward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockOutward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @JsonProperty("itemId")
    @Column(name = "item_id", nullable = false, length = 255)
    private String itemId;

    @Column(name = "issued_to_id")
    private String issuedToId;

    @JsonProperty("issuedTo")
    @Column(name = "recipient_type")
    private String recipientType;

    @Column(name = "reason")
    private String reason; // Renamed from purpose mapping

    @Column(name = "quantity", nullable = false)
    private Integer quantity; // Renamed from quantityRequested mapping

    @Column(name = "returned_quantity")
    private Integer returnedQuantity;

    @Column(name = "issued_by")
    private String issuedBy; // Renamed from requestedBy mapping

    @JsonProperty("linkedReference")
    @Column(name = "reference")
    private String reference;

    @Builder.Default
    @Column(name = "status")
    private String status = "PENDING";

    @JsonProperty("date")
    @Column(name = "issue_date")
    private java.time.LocalDate issueDate; // Changed to LocalDate for frontend compatibility

    @JsonProperty("isReturnable")
    @Column(name = "returnable")
    private Boolean returnable;


    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null)
            this.status = "PENDING";
    }

    // Aliases for compatibility with Service logic
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}