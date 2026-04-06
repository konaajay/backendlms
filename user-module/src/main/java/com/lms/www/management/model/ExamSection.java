package com.lms.www.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_section")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_section_id")
    private Long examSectionId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    @Transient
    private String sectionTitle;

    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder;

    @Transient
    private Integer totalQuestions;

    @Transient
    private Double marksPerQuestion;

    @Builder.Default
    @Transient
    private Boolean isMandatory = true;

    // --- Restored Fields & Aliases for Service Compatibility ---

    @Builder.Default
    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions = false;



    public Boolean getShuffleQuestions() {
        return this.shuffleQuestions;
    }

    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
    }
}