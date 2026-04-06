package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExamProctoring;

@Repository
public interface ExamProctoringRepository extends JpaRepository<ExamProctoring, Long> {

    Optional<ExamProctoring> findByExamId(Long examId);

    boolean existsByExamId(Long examId);
}
