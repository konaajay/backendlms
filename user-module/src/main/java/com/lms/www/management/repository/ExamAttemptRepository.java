package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExamAttempt;

@Repository
public interface ExamAttemptRepository
                extends JpaRepository<ExamAttempt, Long> {

        boolean existsByExamIdAndStudentIdAndStatus(
                        Long examId, Long studentId, String status);

        int countByExamIdAndStudentId(Long examId, Long studentId);

        Optional<ExamAttempt> findFirstByExamIdAndStudentIdAndStatus(
                        Long examId, Long studentId, String status);

        Optional<ExamAttempt> findTopByStudentIdAndExamIdAndStatusOrderByScoreDesc(
                        Long studentId,
                        Long examId,
                        String status);

        List<ExamAttempt> findByStudentId(Long studentId);

        Optional<ExamAttempt> findFirstByStudentIdAndExamIdOrderByStartTimeDesc(Long studentId, Long examId);

        List<ExamAttempt> findByExamId(Long examId);

        List<ExamAttempt> findByExamIdAndStatus(Long examId, String status);

        List<ExamAttempt> findByStudentIdAndExamId(Long studentId, Long examId);
}