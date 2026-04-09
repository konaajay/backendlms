package com.lms.www.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "session_content")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_content_id")
    private Long sessionContentId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content_type", length = 50, nullable = false)
    private String contentType; // VIDEO / PDF / LINK / DOCUMENT

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @jakarta.persistence.Transient
    private Integer orderIndex;

    @Column(name = "total_duration")
    private Integer totalDuration;

    @Column(name = "status")
    private String status; // Added for dashboard metrics

    @Builder.Default
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = "ACTIVE";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
