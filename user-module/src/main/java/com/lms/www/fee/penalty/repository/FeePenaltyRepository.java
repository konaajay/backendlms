package com.lms.www.fee.penalty.repository;

import com.lms.www.fee.penalty.entity.FeePenalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeePenaltyRepository extends JpaRepository<FeePenalty, Long> {
    List<FeePenalty> findByInstallmentId(Long installmentId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(p.amount) FROM FeePenalty p WHERE p.installmentId = :installmentId AND p.waived = false")
    java.math.BigDecimal getTotalPenaltyByInstallmentId(Long installmentId);
}
