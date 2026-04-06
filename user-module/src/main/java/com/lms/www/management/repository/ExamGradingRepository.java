package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExamGrading;

@Repository
public interface ExamGradingRepository extends JpaRepository<ExamGrading, Long> {

    Optional<ExamGrading> findByExamId(Long examId);

    boolean existsByExamId(Long examId);
}
