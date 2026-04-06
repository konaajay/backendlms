package com.lms.www.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attendance_upload_job")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceUploadJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PENDING / PROCESSING / COMPLETED / FAILED

    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) this.status = "PENDING";
        if (this.uploadedAt == null) this.uploadedAt = LocalDateTime.now();
    }
}
