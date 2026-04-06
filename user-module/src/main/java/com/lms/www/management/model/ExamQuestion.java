package com.lms.www.management.model;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_question_id")
    private Long id; // Using id for general compatibility

    @Transient
    private Long examId;

    @Transient
    private Long sectionId;

    @Column(name = "exam_section_id", nullable = false)
    private Long examSectionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "marks")
    private Double marks;

    @Transient
    private Double negativeMarks;

    @Column(name = "question_order")
    private Integer questionOrder;

    // --- Restored Fields & Aliases for Service Compatibility ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;

    public Long getExamQuestionId() {
        return this.id;
    }

    public void setExamQuestionId(Long id) {
        this.id = id;
    }

    public Long getExamSectionId() {
        return this.examSectionId;
    }

    public void setExamSectionId(Long examSectionId) {
        this.examSectionId = examSectionId;
    }
}