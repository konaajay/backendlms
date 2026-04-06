package com.lms.www.management.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.lms.www.management.enums.CertificateRequestStatus;
import com.lms.www.management.enums.TargetType;

@Entity
@Table(name = "certificate_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long requestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "target_type")
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "status")
    private String status; // Stored as String for DB flexibility

    @Transient
    private String rejectionReason;

    @Transient
    private Long processedBy;

    @Transient
    private LocalDateTime processedAt;

    @Builder.Default
    @Column(name = "request_date", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Transient
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Transient
    private String remarks;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_email")
    private String studentEmail;

    @Column(name = "event_title")
    private String eventTitle;

    // --- Restored Fields & Aliases for Service Compatibility ---
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "admin_comment")
    private String adminComment;

    @Column(name = "score")
    private Double score;

    @Column(name = "certificate_id")
    private Long certificateId;

    // --- JPA Mirror Fields for Repository Compatibility ---
    @Column(name = "request_date", insertable = false, updatable = false)
    private LocalDateTime requestDate;

    // --- Aliases & Enum Support ---

    public TargetType getTargetType() {
        return targetType != null ? TargetType.valueOf(targetType) : null;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType != null ? targetType.name() : null;
    }

    public CertificateRequestStatus getStatus() {
        return status != null ? CertificateRequestStatus.valueOf(status) : null;
    }

    public void setStatus(CertificateRequestStatus status) {
        this.status = status != null ? status.name() : null;
    }

    public LocalDateTime getRequestDate() {
        return createdAt;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.createdAt = requestDate;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = CertificateRequestStatus.PENDING.name();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Custom Builder for Enum support
    public static class CertificateRequestBuilder {
        public CertificateRequestBuilder targetType(TargetType targetType) {
            this.targetType = targetType != null ? targetType.name() : null;
            return this;
        }
        
        public CertificateRequestBuilder status(CertificateRequestStatus status) {
            this.status = status != null ? status.name() : null;
            return this;
        }

        public CertificateRequestBuilder requestDate(LocalDateTime requestDate) {
            return createdAt(requestDate);
        }
    }
}
