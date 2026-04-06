package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.CertificateAuditLog;

public interface CertificateAuditLogRepository
        extends JpaRepository<CertificateAuditLog, Long> {

    List<CertificateAuditLog> findByCertificateId(Long certificateId);
}