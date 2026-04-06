package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.Batch;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    Page<Batch> findByTrainerId(Long trainerId, Pageable pageable);

    Optional<Batch> findByBatchIdAndTrainerId(Long batchId, Long trainerId);

    // Get all batches for a course
    List<Batch> findByCourseId(Long courseId);

    // Get a specific batch of a course (if needed)
    Optional<Batch> findByCourseIdAndBatchId(Long courseId, Long batchId);

    // Used for validations (capacity, duplicates, etc.)
    boolean existsByCourseIdAndBatchName(Long courseId, String batchName);
    
    long countByCourseIdAndBatchNameStartingWith(
            Long courseId,
            String batchName
    );
}
