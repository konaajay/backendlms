package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExamQuestion;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {

    @Query("SELECT COUNT(eq) FROM ExamQuestion eq JOIN ExamSection es ON eq.examSectionId = es.examSectionId WHERE es.examId = :examId")
    long countByExamId(@Param("examId") Long examId);

    List<ExamQuestion> findByExamSectionIdOrderByQuestionOrderAsc(Long examSectionId);

    // 🔥 Prevent duplicate question inside same section
    boolean existsByExamSectionIdAndQuestionId(Long examSectionId, Long questionId);

    @Query("""
                SELECT es.examId
                FROM ExamQuestion eq
                JOIN ExamSection es ON eq.examSectionId = es.examSectionId
                WHERE eq.questionId = :questionId
            """)
    Long findExamIdByQuestionId(@Param("questionId") Long questionId);

    @Query("""
                SELECT eq
                FROM ExamQuestion eq, ExamSection es
                WHERE eq.examSectionId = es.examSectionId
                AND es.examId = :examId AND eq.questionId = :questionId
            """)
    Optional<ExamQuestion> findByExamIdAndQuestionId(
            @Param("examId") Long examId,
            @Param("questionId") Long questionId);
}