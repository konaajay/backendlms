package com.lms.www.fee.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminder_jobs", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "installment_id", "reminder_offset" })
})
public class ReminderJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "installment_id", nullable = false)
    private Long installmentId;

    @Column(name = "reminder_offset", nullable = false)
    private Integer reminderOffset;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private JobStatus status = JobStatus.PENDING;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "next_retry_time")
    private LocalDateTime nextRetryTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ReminderJob() {}

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum JobStatus { PENDING, PROCESSING, SUCCESS, FAILED }

    public static ReminderJobBuilder builder() { return new ReminderJobBuilder(); }
    public static class ReminderJobBuilder {
        private ReminderJob job = new ReminderJob();
        public ReminderJobBuilder installmentId(Long id) { job.installmentId = id; return this; }
        public ReminderJobBuilder reminderOffset(Integer offset) { job.reminderOffset = offset; return this; }
        public ReminderJobBuilder scheduledDate(LocalDate date) { job.scheduledDate = date; return this; }
        public ReminderJobBuilder status(JobStatus status) { job.status = status; return this; }
        public ReminderJobBuilder retryCount(Integer count) { job.retryCount = count; return this; }
        public ReminderJobBuilder nextRetryTime(LocalDateTime time) { job.nextRetryTime = time; return this; }
        public ReminderJob build() { return job; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInstallmentId() { return installmentId; }
    public void setInstallmentId(Long id) { this.installmentId = id; }
    public Integer getReminderOffset() { return reminderOffset; }
    public void setReminderOffset(Integer offset) { this.reminderOffset = offset; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate date) { this.scheduledDate = date; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer count) { this.retryCount = count; }
    public LocalDateTime getNextRetryTime() { return nextRetryTime; }
    public void setNextRetryTime(LocalDateTime time) { this.nextRetryTime = time; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
}
