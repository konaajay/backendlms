package com.lms.www.fee.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.payment.entity.StudentFeeRefund;
import java.util.List;

@Repository
public interface StudentFeeRefundRepository extends JpaRepository<StudentFeeRefund, Long> {
    List<StudentFeeRefund> findByAllocationId(Long allocationId);

    @Query("SELECT r FROM StudentFeeRefund r WHERE r.allocationId = :allocationId")
    List<StudentFeeRefund> findByAllocationIdQuery(@Param("allocationId") Long allocationId);

    @Query(value = "SELECT r.* FROM student_fee_refunds r JOIN student_fee_allocations a ON r.student_fee_allocation_id = a.id WHERE a.user_id = :userId", nativeQuery = true)
    List<StudentFeeRefund> findByUserId(@Param("userId") Long userId);
}
