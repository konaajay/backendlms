package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.CertificateRule;

public interface CertificateRuleRepository extends JpaRepository<CertificateRule, Long> {

    Optional<CertificateRule> findByTargetTypeAndTargetIdAndIsEnabledTrue(
            String targetType,
            Long targetId);

    Optional<CertificateRule> findByTargetTypeAndTargetId(
            String targetType,
            Long targetId);
}