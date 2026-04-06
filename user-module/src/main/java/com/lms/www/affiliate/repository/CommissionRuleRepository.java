package com.lms.www.affiliate.repository;

import com.lms.www.affiliate.entity.CommissionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRuleRepository extends JpaRepository<CommissionRule, Long> {
    Optional<CommissionRule> findByCourseIdAndActiveTrue(Long courseId);

    List<CommissionRule> findAllByActiveTrue();
}
