package com.lms.www.fee.penalty.repository;

import com.lms.www.fee.penalty.entity.FeePenaltySlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeePenaltySlabRepository extends JpaRepository<FeePenaltySlab, Long> {
    List<FeePenaltySlab> findByFeeStructureId(Long structureId);
    List<FeePenaltySlab> findByActiveTrue();
}
