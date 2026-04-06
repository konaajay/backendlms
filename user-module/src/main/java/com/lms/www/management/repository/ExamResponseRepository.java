package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExamResponse;
import com.lms.www.management.instructor.dto.PendingEvaluationProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ExamResponseRepository
              extends JpaRepository<ExamResponse, Long> {

       Optional<ExamResponse> findByAttemptIdAndExamQuestionId(
                     Long attemptId, Long examQuestionId);

       List<ExamResponse> findByAttemptId(Long attemptId);

       List<ExamResponse> findByAttemptIdOrderByResponseIdAsc(Long attemptId);

       // ✅ NEW: Get pending evaluations for instructor
       @Query("""
                         SELECT
                             er.responseId AS responseId,
                             er.attemptId AS attemptId,
                             er.descriptiveAnswer AS descriptiveAnswer,
                             er.codingSubmissionCode AS codingSubmissionCode,
                             er.evaluationType AS evaluationType,
                             e.examId AS examId,
                             e.title AS examTitle,
                             er.examQuestionId AS questionId
                         FROM ExamResponse er
                         JOIN ExamAttempt ea ON er.attemptId = ea.id
                         JOIN Exam e ON ea.examId = e.examId
                         WHERE e.createdBy = :instructorId
                           AND er.marksAwarded IS NULL
                     """)
       List<PendingEvaluationProjection> findPendingEvaluationsByInstructorId(Long instructorId);

       // ✅ NEW: Check ownership before evaluation
       @Query("""
                         SELECT COUNT(er)
                         FROM ExamResponse er
                         JOIN ExamAttempt ea ON er.attemptId = ea.id
                         JOIN Exam e ON ea.examId = e.examId
                         WHERE er.responseId = :responseId
                           AND e.createdBy = :instructorId
                     """)
       long checkInstructorOwnership(Long responseId, Long instructorId);
}