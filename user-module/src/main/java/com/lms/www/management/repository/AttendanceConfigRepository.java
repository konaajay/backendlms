package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.AttendanceConfig;

@Repository
public interface AttendanceConfigRepository
        extends JpaRepository<AttendanceConfig, Long> {

    // 🔍 One config per Course + Batch
    Optional<AttendanceConfig> findByCourseIdAndBatchId(
            Long courseId,
            Long batchId
    );

    // ❌ Prevent duplicate configs
    boolean existsByCourseIdAndBatchId(
            Long courseId,
            Long batchId
 
    		);
    
}
