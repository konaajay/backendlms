package com.lms.www.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "attendance_record", uniqueConstraints = {
        @UniqueConstraint(name = "uq_session_student_attendance", columnNames = { "attendance_session_id",
                "student_id" })
})
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attendance_session_id", nullable = false)
    private Long attendanceSessionId;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // PRESENT / ABSENT / LATE

    @Column(name = "remarks", length = 255)
    private String remarks;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "marked_by", nullable = false)
    private Long markedBy;

    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;

    @Builder.Default
    @Column(name = "source", nullable = false, length = 50)
    private String source = "MANUAL"; // MANUAL / QR / FACE / BIOMETRIC / CSV

    @PrePersist
    protected void onCreate() {
        if (this.status == null)
            this.status = "PRESENT";
        if (this.source == null)
            this.source = "MANUAL";
        if (this.markedAt == null)
            this.markedAt = LocalDateTime.now();
    }

    public Long getAttendanceRecordId() {
        return this.id;
    }

    public void setAttendanceRecordId(Long attendanceRecordId) {
        this.id = attendanceRecordId;
    }
}