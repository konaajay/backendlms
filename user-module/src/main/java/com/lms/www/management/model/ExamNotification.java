package com.lms.www.management.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "exam_notification",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "exam_id")
    }
)
public class ExamNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "scheduled_notification", nullable = false)
    private Boolean scheduledNotification;

    // NONE / 1H / 24H
    @Column(name = "reminder_before", nullable = false)
    private String reminderBefore;

    @Column(name = "feedback_after_exam", nullable = false)
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
