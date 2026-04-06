package com.lms.www.management.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lms.www.management.enums.CertificateEligibilityStatus;
import com.lms.www.management.enums.TargetType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certificate_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Named as userId for Builder/Service compatibility

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", length = 50)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "score")
    private BigDecimal score; // BigDecimal for DB/logic

    @Enumerated(EnumType.STRING)
    @Column(name = "eligibility_status")
    private CertificateEligibilityStatus eligibilityStatus;

    @Column(name = "completion_percent")
    private Double completionPercent;

    @Column(name = "attendance_percent")
    private Double attendancePercent;

    @Column(name = "submission_completed")
    private Boolean submissionCompleted;

    @Transient
    private String criteriaMetadataJson;

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getStudentId() {
        return this.userId;
    }

    public void setStudentId(Long studentId) {
        this.userId = studentId;
    }

    // Alias for Double score from service
    public void setScore(Double score) {
        if (score == null) {
            this.score = null;
        } else {
            this.score = BigDecimal.valueOf(score);
        }
    }

    public Double getScoreAsDouble() {
        return score != null ? score.doubleValue() : null;
    }

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        if (this.eligibilityStatus == null) this.eligibilityStatus = CertificateEligibilityStatus.IN_PROGRESS;
    }
}