package com.lms.www.management.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "certificate_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_id", nullable = false)
    private Long certificateId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "performed_by")
    private Long performedBy;

    @Builder.Default
    @Column(name = "action_date", nullable = false)
    private LocalDateTime actionDate = LocalDateTime.now();

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks; // Renamed back from details

    @PrePersist
    protected void onCreate() {
        if (this.actionDate == null) this.actionDate = LocalDateTime.now();
    }
}