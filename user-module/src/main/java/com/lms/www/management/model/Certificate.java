package com.lms.www.management.model;

import java.time.LocalDateTime;

import com.lms.www.management.enums.CertificateStatus;
import com.lms.www.management.enums.TargetType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "certificate")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "certificate_id", unique = true, nullable = false)
    private String certificateId; // Real certificate number (e.g. CERT-123)

    @Column(name = "user_id", nullable = false)
    private Long studentId;

    @Column(name = "template_id", nullable = false)
    private Long templateId;

    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CertificateStatus status = CertificateStatus.ACTIVE;

    @Column(name = "verification_token")
    private String verificationToken;

    @Builder.Default
    @Column(name = "version")
    private Integer version = 1;

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "revoked_reason")
    private String revokedReason;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_email")
    private String studentEmail;

    @Column(name = "event_title")
    private String eventTitle;

    @Column(name = "score")
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type")
    private TargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    // --- Aliases for Service/Legacy Compatibility ---
    
    // Some code uses getCertificateNumber/setCertificateNumber
    public String getCertificateNumber() { return certificateId; }
    public void setCertificateNumber(String val) { this.certificateId = val; }

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "issued_date", insertable = false, updatable = false)
    private LocalDateTime issuedDate;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = CertificateStatus.ACTIVE;
        if (this.version == null) this.version = 1;
        if (this.issueDate == null) this.issueDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}