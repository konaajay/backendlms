package com.lms.www.fee.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.payment.entity.EarlyPayment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EarlyPaymentRepository extends JpaRepository<EarlyPayment, Long> {

    Optional<EarlyPayment> findByCashfreeOrderId(String cashfreeOrderId);
    List<EarlyPayment> findByStudentId(Long studentId);

    List<EarlyPayment> findByStatusAndLinkExpiryBefore(
            EarlyPayment.EarlyPaymentStatus status,
            LocalDateTime expiry
    );

    List<EarlyPayment> findByStudentIdAndStatus(
            Long studentId,
            EarlyPayment.EarlyPaymentStatus status
    );

    @Query("""
        SELECT e FROM EarlyPayment e
        WHERE e.studentId = :studentId
        AND e.status = :status
        AND e.linkExpiry > :now
    """)
    List<EarlyPayment> findActivePayments(
            Long studentId,
            EarlyPayment.EarlyPaymentStatus status,
            LocalDateTime now
    );
}
