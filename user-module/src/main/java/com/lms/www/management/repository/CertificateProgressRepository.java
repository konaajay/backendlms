package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.enums.CertificateEligibilityStatus;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.CertificateProgress;

public interface CertificateProgressRepository
        extends JpaRepository<CertificateProgress, Long> {

    Optional<CertificateProgress> findByUserIdAndTargetTypeAndTargetId(
            Long userId,
            TargetType targetType,
            Long targetId
    );
    
    List<CertificateProgress> findByTargetTypeAndTargetIdAndEligibilityStatus(
            TargetType targetType,
            Long targetId,
            CertificateEligibilityStatus eligibilityStatus
    );
}