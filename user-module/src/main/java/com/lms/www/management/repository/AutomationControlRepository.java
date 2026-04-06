package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.model.AutomationControl;

@Repository
public interface AutomationControlRepository extends JpaRepository<AutomationControl, Long> {
    Optional<AutomationControl> findByRuleName(String ruleName);
}