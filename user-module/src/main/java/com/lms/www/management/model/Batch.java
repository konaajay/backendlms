package com.lms.www.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "batch")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    // ✅ Single course reference (correct design)
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_name", nullable = false)
    private String batchName;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @Column(name = "trainer_name", nullable = false)
    private String trainerName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_students")
    private Integer maxStudents;

    // ===============================
    // 💰 FREE OR PAID
    // ===============================
    @Column(name = "free_batch", nullable = false)
    private Boolean freeBatch;

    @Column(name = "fee")
    private Double fee;

    // ===============================
    // 🔒 CONTENT ACCESS
    // ===============================
    @Column(name = "content_access", nullable = false)
    private Boolean contentAccess;

    @Column(name = "status")
    private String status; // Upcoming / Running / Completed

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===============================
    // LIFECYCLE
    // ===============================
    @PrePersist
    protected void onCreate() {
        this.status = "Upcoming";

        if (this.freeBatch == null) {
            this.freeBatch = true;
        }

        if (this.contentAccess == null) {
            this.contentAccess = false;
        }

        if (Boolean.TRUE.equals(this.freeBatch)) {
            this.fee = null;
        }

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (Boolean.TRUE.equals(this.freeBatch)) {
            this.fee = null;
        }
        this.updatedAt = LocalDateTime.now();
    }

    // ===============================
    // GETTERS & SETTERS
    // ===============================
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }

    public String getTrainerName() { return trainerName; }
    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }

    public Boolean getFreeBatch() { return freeBatch; }
    public void setFreeBatch(Boolean freeBatch) { this.freeBatch = freeBatch; }

    public Double getFee() { return fee; }
    public void setFee(Double fee) { this.fee = fee; }

    public Boolean getContentAccess() { return contentAccess; }
    public void setContentAccess(Boolean contentAccess) { this.contentAccess = contentAccess; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}