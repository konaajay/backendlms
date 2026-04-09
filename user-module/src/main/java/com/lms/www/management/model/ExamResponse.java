package com.lms.www.management.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_response")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @Transient
    private Long questionId;

    @Column(name = "exam_question_id", nullable = false)
    private Long examQuestionId; // Keeping this for backward compatibility with service layer

    @Column(name = "selected_option_id")
    private Long selectedOptionId;

    @Column(name = "descriptive_answer", columnDefinition = "TEXT")
    private String descriptiveAnswer;

    @Transient
    private Double marksObtained;

    @Column(name = "marks_awarded")
    private Double marksAwarded; // Consistent with service layer terminology

    @Transient
    private Boolean isCorrect;

    @Builder.Default
    @Transient
    private Boolean isReviewed = false;

    @Transient
    private Long reviewedBy;

    @Transient
    private LocalDateTime reviewedAt;

    @Column(name = "evaluation_type")
    private String evaluationType;

    @Column(name = "coding_submission_code", columnDefinition = "LONGTEXT")
    private String codingSubmissionCode;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Builder.Default
    @Transient
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Transient
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Transient
    private Long questionIdForDto; // For controller logic if needed

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isReviewed == null) this.isReviewed = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
