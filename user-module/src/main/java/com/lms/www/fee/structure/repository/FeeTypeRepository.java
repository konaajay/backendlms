package com.lms.www.fee.structure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lms.www.fee.structure.entity.FeeType;
import java.util.List;
import java.util.Optional;

public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {

    boolean existsByNameIgnoreCase(String name);

    List<FeeType> findByActiveTrue();

    Optional<FeeType> findByNameIgnoreCase(String name);
}
