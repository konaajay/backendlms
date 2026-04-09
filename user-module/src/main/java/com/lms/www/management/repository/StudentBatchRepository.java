package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lms.www.management.model.StudentBatch;

public interface StudentBatchRepository
        extends JpaRepository<StudentBatch, Long> {

    Page<StudentBatch> findByBatchId(Long batchId, Pageable pageable);

    @Query("SELECT sb FROM StudentBatch sb JOIN sb.batch b WHERE sb.studentId = :studentId AND b.trainerId = :trainerId")
    Optional<StudentBatch> findByStudentIdAndTrainerId(@Param("studentId") Long studentId, @Param("trainerId") Long trainerId);

    // View students in a batch
    List<StudentBatch> findByBatchId(Long batchId);

    // View student's current active batch
    Optional<StudentBatch> findFirstByStudentIdAndStatus(
            Long studentId, String status);

    // Validation checks
    boolean existsByStudentIdAndBatchIdAndStatus(
            Long studentId, Long batchId, String status);

    boolean existsByStudentIdAndCourseIdAndStatus(
            Long studentId, Long courseId, String status);

    // ⭐ REQUIRED for batch capacity check
    long countByBatchIdAndStatus(Long batchId, String status);
    
    @Query(
            value = """
                SELECT DISTINCT CAST(student_id AS SIGNED)
                FROM student_batch
                WHERE batch_id = :batchId
            """,
            nativeQuery = true
        )
        List<Long> findStudentIdsByBatchId(
                @Param("batchId") Long batchId
        );
    
    Optional<StudentBatch> findFirstByStudentIdAndCourseIdAndStatus(
            Long studentId,
            Long courseId,
            String status
    );
    
    List<StudentBatch> findByUserId(Long userId);
    
    List<StudentBatch> findByStudentId(Long studentId);
    
    @Query("SELECT sb FROM StudentBatch sb JOIN FETCH sb.batch WHERE sb.studentId = :studentId")
    List<StudentBatch> findByStudentIdWithBatch(@Param("studentId") Long studentId);
}
