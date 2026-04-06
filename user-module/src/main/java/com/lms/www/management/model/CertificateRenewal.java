package com.lms.www.management.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "certificate_renewal")
public class CertificateRenewal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_id", nullable = false)
    private Long certificateId;

    @Column(name = "renewed_on", nullable = false)
    private LocalDateTime renewalDate;

    @Column(name = "previous_expiry")
    private LocalDateTime previousExpiry; // Restoration

    @Column(name = "new_expiry", nullable = false)
    private LocalDateTime newExpiry;

    @Builder.Default
    @Transient
    private String status = "COMPLETED";

    @Column(name = "renewed_by")
    private Long renewedBy; // Added for service logic

    @Column(name = "remarks")
    private String remarks; // Added for service logic

    @PrePersist
    protected void onCreate() {
        if (this.renewalDate == null) this.renewalDate = LocalDateTime.now();
        if (this.status == null) this.status = "COMPLETED";
    }

    // Alias for builder compatibility if service uses renewedOn
    public static class CertificateRenewalBuilder {
        public CertificateRenewalBuilder renewedOn(LocalDateTime date) {
            this.renewalDate = date;
            return this;
        }
    }
}