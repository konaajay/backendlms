package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;
@Entity
@Table(name = "exam_attempt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long attemptId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "attempt_number")
    private Integer attemptNumber; // Restoration

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "score")
    private Double score;

    @Transient
    private Double scoreObtained;

    @Transient
    private Double percentageObtained;

    @Transient
    private String resultStatus; // PASS / FAIL / PENDING

    @Builder.Default
    @Column(name = "status")
    private String status = "IN_PROGRESS"; // IN_PROGRESS / SUBMITTED / EVALUATED / EXPIRED

    @Transient
    private Integer proctoringViolationCount;

    @Transient
    private String ipAddress;

    @Transient
    private String browserInfo;

    @Transient
    private LocalDateTime lastHeartbeat;

    // --- JPA Mirror Fields for Repository Compatibility ---
    @Column(name = "attempt_id", insertable = false, updatable = false)
    private Long id;

    @Builder.Default
    @Transient
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "IN_PROGRESS";
        if (this.startTime == null) this.startTime = LocalDateTime.now();
    }

    // Aliases for Service Compatibility
    public Long getId() { return attemptId; }
    public Double getScoreObtained() { return score; }
    public void setScoreObtained(Double scoreObtained) { this.score = scoreObtained; }
}
