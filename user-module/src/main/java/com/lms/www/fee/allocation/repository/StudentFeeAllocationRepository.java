package com.lms.www.fee.allocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeAllocationRepository extends JpaRepository<StudentFeeAllocation, Long> {
        List<StudentFeeAllocation> findByUserId(Long userId);

        List<StudentFeeAllocation> findByUserIdIn(List<Long> userIds);

        Optional<StudentFeeAllocation> findByUserIdAndFeeStructureId(Long userId, Long feeStructureId);

        boolean existsByUserIdAndFeeStructureId(Long userId, Long feeStructureId);

        boolean existsByUserIdAndBatchId(Long userId, Long batchId);

        List<StudentFeeAllocation> findByUserIdAndBatchId(Long userId, Long batchId);

        @Query("SELECT a FROM StudentFeeAllocation a WHERE a.batchId = :batchId")
        List<StudentFeeAllocation> findByBatchId(Long batchId);
}
