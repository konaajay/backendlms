package com.lms.www.fee.structure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.structure.entity.FeeStructureComponent;
import java.util.List;

@Repository
public interface FeeStructureComponentRepository extends JpaRepository<FeeStructureComponent, Long> {
    List<FeeStructureComponent> findByFeeStructureId(Long feeStructureId);
}
