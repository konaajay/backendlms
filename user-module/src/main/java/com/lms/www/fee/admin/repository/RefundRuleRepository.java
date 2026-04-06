package com.lms.www.fee.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.admin.entity.RefundRule;
import java.util.List;

@Repository
public interface RefundRuleRepository extends JpaRepository<RefundRule, Long> {
    List<RefundRule> findByActiveTrue();
}
