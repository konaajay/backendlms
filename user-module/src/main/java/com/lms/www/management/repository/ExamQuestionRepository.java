package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExamQuestion;

@Repository
public interface ExamQuestionRepository
        extends JpaRepository<ExamQuestion, Long> {

    List<ExamQuestion> findByExamSectionIdOrderByQuestionOrderAsc(Long examSectionId);

    // 🔥 Prevent duplicate question inside same section
    boolean existsByExamSectionIdAndQuestionId(Long examSectionId, Long questionId);

    @Query("""
                SELECT es.examId
                FROM ExamQuestion eq
                JOIN ExamSection es ON eq.examSectionId = es.examSectionId
                WHERE eq.questionId = :questionId
            """)
    Long findExamIdByQuestionId(@org.springframework.data.repository.query.Param("questionId") Long questionId);

    @Query("""
                SELECT eq
                FROM ExamQuestion eq, ExamSection es
                WHERE eq.examSectionId = es.examSectionId
                AND es.examId = :examId AND eq.questionId = :questionId
            """)
    java.util.Optional<ExamQuestion> findByExamIdAndQuestionId(
            @org.springframework.data.repository.query.Param("examId") Long examId,
            @org.springframework.data.repository.query.Param("questionId") Long questionId);
}