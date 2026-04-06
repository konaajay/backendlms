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
@Table(name = "exam_violation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_id")
    private Long violationId;

    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @Column(name = "violation_type", nullable = false, length = 50)
    private String violationType;

    @Column(name = "violation_time", nullable = false)
    private LocalDateTime violationTime;

    @Transient
    private String details;

    @PrePersist
    protected void onCreate() {
        if (this.violationTime == null) this.violationTime = LocalDateTime.now();
    }
}
