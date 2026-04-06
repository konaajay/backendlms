package com.lms.www.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.management.enums.CertificateRequestStatus;
import com.lms.www.management.enums.TargetType;
import com.lms.www.management.model.CertificateRequest;

@Repository
public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {

    List<CertificateRequest> findAllByStatus(CertificateRequestStatus status);

    Optional<CertificateRequest> findByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType,
            Long targetId);

    boolean existsByUserIdAndTargetTypeAndTargetIdAndStatus(Long userId, TargetType targetType, Long targetId,
            CertificateRequestStatus status);
    
    List<CertificateRequest> findAllByStatusOrderByRequestDateDesc(CertificateRequestStatus status);
}
