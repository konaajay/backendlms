package com.lms.www.fee.discount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.www.fee.discount.entity.FeeDiscount;
import java.util.List;
import java.util.Optional;

public interface FeeDiscountRepository extends JpaRepository<FeeDiscount, Long> {
    List<FeeDiscount> findByUserId(Long userId);

    List<FeeDiscount> findByUserIdAndIsActive(Long userId, Boolean isActive);

    List<FeeDiscount> findByFeeStructureId(Long feeStructureId);

    List<FeeDiscount> findByUserIdAndFeeStructureId(Long userId, Long feeStructureId);

    Optional<FeeDiscount> findTopByUserIdAndFeeStructureIdAndIsActiveTrueOrderByIdDesc(
            Long userId,
            Long feeStructureId);
}
