package com.lms.www.fee.installment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan;
import com.lms.www.fee.installment.entity.StudentInstallmentPlan.InstallmentStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentInstallmentPlanRepository extends JpaRepository<StudentInstallmentPlan, Long> {

    List<StudentInstallmentPlan> findByStudentFeeAllocationId(Long allocationId);

    void deleteByStudentFeeAllocationId(Long allocationId);

    List<StudentInstallmentPlan> findByStudentFeeAllocationIdAndStatus(Long allocationId, InstallmentStatus status);

    List<StudentInstallmentPlan> findByStatus(InstallmentStatus status);

    Optional<StudentInstallmentPlan> findByCashfreeOrderId(String cashfreeOrderId);

    @Query("SELECT s FROM StudentInstallmentPlan s WHERE s.status = :status AND s.dueDate BETWEEN :from AND :to")
    List<StudentInstallmentPlan> findPendingInstallmentsDueBetween(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("status") InstallmentStatus status);

    @Query("SELECT s FROM StudentInstallmentPlan s WHERE s.dueDate < :today AND s.status <> :status")
    List<StudentInstallmentPlan> findOverdueInstallments(@Param("today") LocalDate today,
            @Param("status") InstallmentStatus status);

    List<StudentInstallmentPlan> findByStatusIn(List<InstallmentStatus> statuses);

    @Query("SELECT s FROM StudentInstallmentPlan s WHERE s.studentFeeAllocationId IN (SELECT a.id FROM com.lms.www.fee.allocation.entity.StudentFeeAllocation a WHERE a.batchId = :batchId)")
    List<StudentInstallmentPlan> findByBatchId(@Param("batchId") Long batchId);

    boolean existsByStudentFeeAllocationId(Long allocationId);
}
