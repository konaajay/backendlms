package com.lms.www.affiliate.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliate_leads", indexes = {
    @Index(name = "idx_lead_mobile_batch", columnList = "mobile, batch_id", unique = true),
    @Index(name = "idx_lead_affiliate", columnList = "affiliate_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliateLead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String email;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = true)
    private Affiliate affiliate;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "link_id")
    private Long linkId;

    @Column(name = "referral_code")
    private String referralCode;

    @Column(name = "lead_source")
    private String leadSource; // e.g., "AFFILIATE"

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        expiresAt = createdAt.plusDays(30);
        if (status == null) status = LeadStatus.NEW;
        if (leadSource == null) leadSource = "AFFILIATE";
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum LeadStatus {
        NEW, CONTACTED, INTERESTED, ENROLLED, REJECTED, LOST
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
