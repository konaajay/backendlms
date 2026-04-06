package com.lms.www.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.CertificateRenewal;

public interface CertificateRenewalRepository
        extends JpaRepository<CertificateRenewal, Long> {
}