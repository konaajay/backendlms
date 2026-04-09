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
@Table(name = "exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "exam_type", nullable = false)
    private String examType; // ENTRANCE / SEMESTER / PRACTICE

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "pass_percentage", nullable = false)
    private Double passPercentage;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Builder.Default
    @Column(name = "status", nullable = false)
    private String status = "DRAFT";

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    // --- Essential Fields for Services ---
    
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "created_by")
    private Long createdBy;

    @Builder.Default
    @Column(name = "certificate_enabled")
    private Boolean certificateEnabled = false;

    @Column(name = "certificate_template_id")
    private Long certificateTemplateId;

    // --- Transient fields for Dashboard ---
    @jakarta.persistence.Transient
    private Long questionCount;

    @jakarta.persistence.Transient
    private String courseName;

    @jakarta.persistence.Transient
    private String batchName;

    @jakarta.persistence.Transient
    private LocalDateTime endTime;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "DRAFT";
        if (this.isDeleted == null) this.isDeleted = false;
        if (this.certificateEnabled == null) this.certificateEnabled = false;
    }
}
