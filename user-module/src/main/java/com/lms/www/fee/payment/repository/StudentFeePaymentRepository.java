package com.lms.www.fee.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.payment.entity.StudentFeePayment;
import com.lms.www.fee.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeePaymentRepository extends JpaRepository<StudentFeePayment, Long> {
    List<StudentFeePayment> findByStudentFeeAllocationId(Long allocationId);

    Optional<StudentFeePayment> findByTransactionReference(String transactionReference);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<StudentFeePayment> findByCashfreeOrderId(String cashfreeOrderId);

    boolean existsByTransactionReference(String transactionReference);

    @Query("SELECT p FROM StudentFeePayment p JOIN StudentFeeAllocation a ON p.studentFeeAllocationId = a.id WHERE a.userId = :studentId")
    List<StudentFeePayment> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT SUM(p.paidAmount) FROM StudentFeePayment p WHERE p.studentFeeAllocationId = :allocationId")
    BigDecimal getTotalPaidByAllocationId(@Param("allocationId") Long allocationId);

    @Query("SELECT p FROM StudentFeePayment p JOIN FETCH p.allocation")
    List<StudentFeePayment> findAllWithAllocation();

    @Query("SELECT p FROM StudentFeePayment p JOIN StudentFeeAllocation a ON p.studentFeeAllocationId = a.id WHERE a.userId = :studentId ORDER BY p.paymentDate DESC")
    List<StudentFeePayment> findTop5ByStudentIdOrderByPaymentDateDesc(@Param("studentId") Long studentId);

    Optional<StudentFeePayment> findByStudentFeeAllocationIdAndPaymentStatus(Long allocationId, PaymentStatus status);
    List<StudentFeePayment> findByPaymentStatusAndPaymentDateBefore(PaymentStatus status, java.time.LocalDateTime threshold);
}
