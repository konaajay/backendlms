package com.lms.www.management.model;

import java.time.LocalDateTime;

import com.lms.www.management.enums.ProgressStatus;

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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "student_video_progress",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "session_content_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class StudentVideoProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "session_content_id", nullable = false)
    private Long sessionContentId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId; 
    // Stored for fast session-level aggregation

    @Column(name = "watched_duration", nullable = false)
    private Long watchedDuration = 0L; 
    // Total accumulated watch time in seconds

    @Column(name = "last_watched_position", nullable = false)
    private Long lastWatchedPosition = 0L; 
    // Last player position in seconds

    @Column(name = "total_duration_snapshot", nullable = false)
    private Long totalDurationSnapshot; 
    // Snapshot of video duration when progress started

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @PrePersist
    protected void onCreate() {
        this.status = ProgressStatus.NOT_STARTED;
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedAt = LocalDateTime.now();
    }
    
    @Column(name = "percentage_watched")
    private Double percentageWatched;
    
    @Column(name = "user_id")
    private Long userId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getSessionContentId() { return sessionContentId; }
    public void setSessionContentId(Long sessionContentId) { this.sessionContentId = sessionContentId; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getWatchedDuration() { return watchedDuration; }
    public void setWatchedDuration(Long watchedDuration) { this.watchedDuration = watchedDuration; }
    public Long getLastWatchedPosition() { return lastWatchedPosition; }
    public void setLastWatchedPosition(Long lastWatchedPosition) { this.lastWatchedPosition = lastWatchedPosition; }
    public Long getTotalDurationSnapshot() { return totalDurationSnapshot; }
    public void setTotalDurationSnapshot(Long totalDurationSnapshot) { this.totalDurationSnapshot = totalDurationSnapshot; }
    public ProgressStatus getStatus() { return status; }
    public void setStatus(ProgressStatus status) { this.status = status; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    public Double getPercentageWatched() { return percentageWatched; }
    public void setPercentageWatched(Double percentageWatched) { this.percentageWatched = percentageWatched; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}

