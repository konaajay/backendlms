package com.lms.www.management.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.enums.CertificateStatus;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    // Verify certificate using secure token
    Optional<Certificate> findByVerificationToken(String verificationToken);

    // Check duplicate certificate for same user & event
    boolean existsByUserIdAndTargetTypeAndTargetId(
            Long userId,
            TargetType targetType,
            Long targetId
    );

    // Get all certificates of a user
    List<Certificate> findByUserId(Long userId);

    // Get certificates by status (Admin filter)
    List<Certificate> findByStatus(CertificateStatus status);

    // Get expiring certificates (for reminder job)
    
    List<Certificate> findByExpiryDateBeforeAndStatus(
            LocalDateTime expiryDate,
            CertificateStatus status
    );
    
    long countByStatus(CertificateStatus status);

    long countByIssuedDateAfter(LocalDateTime dateTime);
}
