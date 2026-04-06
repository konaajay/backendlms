package com.lms.www.management.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sessionId;

    @Column(nullable = false)
    private Long fromUserId; // Student ID

    @Column(nullable = false)
    private Long toUserId; // Instructor ID

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Manual accessors to resolve Lombok processing issues
    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getRating() { return rating; }
    public void setComment(String comment) { this.comment = comment; }
    public String getComment() { return comment; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public Long getSessionId() { return sessionId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }
    public Long getFromUserId() { return fromUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
    public Long getToUserId() { return toUserId; }
}