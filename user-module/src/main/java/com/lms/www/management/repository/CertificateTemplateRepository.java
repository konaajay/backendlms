package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.CertificateTemplate;

public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, Long> {
    // (Your existing methods)
    Optional<CertificateTemplate> findFirstByIsActiveTrue();

    // 1. Find by Target Type AND Target ID AND Active
    Optional<CertificateTemplate> findFirstByTargetTypeAndTargetIdAndIsActiveTrue(TargetType targetType, Long targetId);

    // 2. Find by Target Type AND Active where Target ID is NULL
    Optional<CertificateTemplate> findFirstByTargetTypeAndTargetIdIsNullAndIsActiveTrue(TargetType targetType);
}