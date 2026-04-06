package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.ExamDesign;

@Repository
public interface ExamDesignRepository extends JpaRepository<ExamDesign, Long> {

    Optional<ExamDesign> findByExamId(Long examId);

    boolean existsByExamId(Long examId);
}
