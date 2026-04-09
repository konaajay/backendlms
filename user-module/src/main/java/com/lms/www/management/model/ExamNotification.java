package com.lms.www.management.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(
    name = "exam_notification",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "exam_id")
    }
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExamNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "scheduled_notification", nullable = false)
    @JsonProperty("scheduled_notification")
    private Boolean scheduledNotification;

    // NONE / 1H / 24H
    @Column(name = "reminder_before", nullable = false)
    @JsonProperty("reminder_before")
    private String reminderBefore;

    @Column(name = "feedback_after_exam", nullable = false)
    @JsonProperty("feedback_after_exam")
    private Boolean feedbackAfterExam;

    public ExamNotification() {}

    // Manual Getters and Setters
    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public Long getExamId() { return examId; }
    public void setExamId(Long examId) { this.examId = examId; }

    public Boolean getScheduledNotification() { return scheduledNotification; }
    public void setScheduledNotification(Boolean scheduledNotification) { this.scheduledNotification = scheduledNotification; }

    public String getReminderBefore() { return reminderBefore; }
    public void setReminderBefore(String reminderBefore) { this.reminderBefore = reminderBefore; }

    public Boolean getFeedbackAfterExam() { return feedbackAfterExam; }
    public void setFeedbackAfterExam(Boolean feedbackAfterExam) { this.feedbackAfterExam = feedbackAfterExam; }
}
