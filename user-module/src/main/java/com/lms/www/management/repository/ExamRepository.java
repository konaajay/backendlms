package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    Page<Exam> findByCreatedByAndIsDeletedFalse(Long createdBy, Pageable pageable);

    Optional<Exam> findByExamIdAndCreatedByAndIsDeletedFalse(Long examId, Long createdBy);

    // Get single exam (not deleted)
    Optional<Exam> findByExamIdAndIsDeletedFalse(Long examId);

    List<Exam> findByIsDeletedTrue();

    List<Exam> findByBatchIdInAndIsDeletedFalse(List<Long> batchIds);
}
